//
//  MessageDispatcher.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "MessageDispatcher.h"
#import "BodyInterpreter.h"
#import "CKJsonParser.h"
#import "FriendManager.h"
#import "AppDelegate.h"
#import "NotificationManager.h"


@interface MessageDispatcher ()
@property NSMutableArray *pendingQueue;
@property NSMutableArray *chatRealCache;
@property NSNotificationCenter *notiCenter;
@end

@implementation MessageDispatcher

MessageDispatcher *instance;
long lastDT=0;
int lastDTCnt=0;

+(MessageDispatcher*)getInstance{
    if(instance == nil)
        instance = [[MessageDispatcher alloc]init];
    return instance;
}

-(instancetype)init{
    self = [super init];
    if(self != nil){
        _pendingQueue = [NSMutableArray array];
        self.notiCenter = [NSNotificationCenter defaultCenter];
    }
    return self;
}

-(void)sendSuccess:(long)datetime{
    Message *msg = self.pendingQueue[0];
    Friend *friend = [Friend objectForPrimaryKey:msg.roomAddress];
    [self.pendingQueue removeObjectAtIndex:0];
    if(lastDT==datetime)
        lastDTCnt ++;
    else{
        lastDTCnt = 0;
        lastDT = datetime;
    }
    RLMRealm *realm = [self chatRealmWithFriend:friend];
    [realm beginWriteTransaction];
    msg.datetime = datetime;
    msg.idx = lastDTCnt;
    [realm commitWriteTransaction];
    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":msg}];
}

-(void)newChatMessage:(NSDictionary*)dictionary{
    NSLog(@"Chat Message Arrived ::\n%@",dictionary);
    NSString *address = dictionary[(__bridge NSString *) KEY_ADDRESS];
    if([address containsString:@"system_"])
        return [self newSystemMessage:address withData:dictionary];
    NSDictionary *msgJson = [CKJsonParser parseJson:dictionary[(__bridge NSString *) KEY_MESSAGE_JSON]]; //todo Decrypt
    Message *msg =[[Message alloc]init];

    Friend *friend = [Friend objectForPrimaryKey:address];

    msg.text        = msgJson[@"text"];
    msg.url         = msgJson[@"url"];
    msg.type        = msgJson[@"type"];
    msg.datetime    = [dictionary[(__bridge NSString *) KEY_SEND_DATETIME] longLongValue];
    msg.idx         = [dictionary[(__bridge NSString *) KEY_INDEX] intValue];
    msg.roomAddress = address;
    msg.mine        = false;

    RLMRealm *realm = [self chatRealmWithFriend:friend];
    Message *result;
    [realm beginWriteTransaction];
    result = [Message createInRealm:realm withValue:msg];
    [realm commitWriteTransaction];
    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":result}];
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSLog(@"new msg");
    if([appDelegate isInBackground]){
        NSLog(@"new msg");
        [[NotificationManager getInstance] pushNotification:[NSString stringWithFormat:@"%@ : %@",friend.nickname,msg.text]
                                                  withTitle:friend.nickname withAction:@"답장하기"
                                               withUserInfo:@{@"msgType":@"new_msg",@"data":friend.address}];
    }
    receiveSuccessfully(msg.roomAddress, msg.datetime, msg.idx);
}

-(void)newSystemMessage:(NSString*)systemUserAddr withData:(NSDictionary *)dictionary{
    long dt = (long) [dictionary[(__bridge NSString *) KEY_SEND_DATETIME] longLongValue];
    int idx = [dictionary[(__bridge NSString *) KEY_INDEX] intValue];
    if([systemUserAddr isEqualToString:@"system_matchmaker"])
        [[FriendManager getInstance] messageReceivedFromMatchmaker:dictionary[(__bridge NSString *)KEY_MESSAGE_JSON]];
    receiveSuccessfully(systemUserAddr,dt,idx);
}

-(Message*)sendMessage:(Message*)msg toFriend:(Friend*)friend{
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    dic[@"type"] = msg.type;
    if(msg.text)
        dic[@"text"] = msg.text;
    if(msg.url)
        dic[@"url"] = msg.url;

    RLMRealm *realm = [self chatRealmWithFriend:friend];
    Message *result;
    [realm beginWriteTransaction];
    result = [Message createInRealm:realm withValue:msg];
    [realm commitWriteTransaction];
    [self.pendingQueue addObject:result];
    sendMsg(msg.roomAddress, [CKJsonParser serializeObject:dic]);
    return result;
}

-(RLMRealm*)chatRealmWithFriend:(Friend*)friend{
    NSString *path = [Friend chatRealmPathWithName:friend.address];
    for(RLMRealm *realm in self.chatRealCache)
        if([realm.path isEqualToString:path])
            return realm;
    
    if(self.chatRealCache.count >= 10)
       [self.chatRealCache removeObjectAtIndex:0];
    NSData *key = [[NSData alloc] initWithBase64EncodedString:friend.encKey options:0];

    RLMRealm *realm = [RLMRealm realmWithPath:path encryptionKey:key readOnly:NO error:NULL];
    
    [self.chatRealCache addObject:realm];
    return realm;
}

@end
