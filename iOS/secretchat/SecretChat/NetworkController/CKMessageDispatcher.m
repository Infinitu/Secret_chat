//
//  CKMessageDispatcher.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKMessageDispatcher.h"
#import "CKBodyInterpreter.h"
#import "CKJsonParser.h"
#import "CKFriendManager.h"
#import "AppDelegate.h"
#import "CKNotificationManager.h"


@interface CKMessageDispatcher ()
@property NSMutableArray *pendingQueue;
@property NSMutableArray *chatRealCache;
@property NSNotificationCenter *notiCenter;
@end

@implementation CKMessageDispatcher

CKMessageDispatcher *instance;
long lastDT=0;
int lastDTCnt=0;

+(CKMessageDispatcher *)getInstance{
    if(instance == nil)
        instance = [[CKMessageDispatcher alloc]init];
    return instance;
}

-(instancetype)init{
    self = [super init];
    if(self != nil){
        _pendingQueue = [NSMutableArray array];
        self.notiCenter = [NSNotificationCenter defaultCenter];
        self.chatRealCache = [NSMutableArray array];
    }
    return self;
}

-(void)sendSuccess:(long)datetime{
    CKMessage *msg = self.pendingQueue[0];
    CKFriend *friend = [CKFriend objectForPrimaryKey:msg.roomAddress];
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
    CKMessage *msg =[[CKMessage alloc]init];

    CKFriend *friend = [CKFriend objectForPrimaryKey:address];

    msg.text        = msgJson[@"text"];
    msg.url         = msgJson[@"url"];
    msg.type        = msgJson[@"type"];
    msg.datetime    = [dictionary[(__bridge NSString *) KEY_SEND_DATETIME] longLongValue];
    msg.idx         = [dictionary[(__bridge NSString *) KEY_INDEX] intValue];
    msg.roomAddress = address;
    msg.mine        = false;

    RLMRealm *realm = [self chatRealmWithFriend:friend];
    CKMessage *result;
    [realm beginWriteTransaction];
    result = [CKMessage createInRealm:realm withValue:msg];
    [realm commitWriteTransaction];
    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":result}];
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    NSLog(@"new msg");
    if([appDelegate isInBackground]){
        NSLog(@"new msg");
        [[CKNotificationManager getInstance] pushNotification:[NSString stringWithFormat:@"%@ : %@",friend.nickname,msg.text]
                                                  withTitle:friend.nickname withAction:@"답장하기"
                                               withUserInfo:@{@"msgType":@"new_msg",@"data":friend.address}];
    }
    receiveSuccessfully(msg.roomAddress, msg.datetime, msg.idx);
}

-(void)newSystemMessage:(NSString*)systemUserAddr withData:(NSDictionary *)dictionary{
    long dt = (long) [dictionary[(__bridge NSString *) KEY_SEND_DATETIME] longLongValue];
    int idx = [dictionary[(__bridge NSString *) KEY_INDEX] intValue];
    if([systemUserAddr isEqualToString:@"system_matchmaker"])
        [[CKFriendManager getInstance] messageReceivedFromMatchmaker:dictionary[(__bridge NSString *)KEY_MESSAGE_JSON]];
    receiveSuccessfully(systemUserAddr,dt,idx);
}

-(CKMessage *)sendMessage:(CKMessage *)msg toFriend:(CKFriend *)friend{
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    dic[@"type"] = msg.type;
    if(msg.text)
        dic[@"text"] = msg.text;
    if(msg.url)
        dic[@"url"] = msg.url;

    RLMRealm *realm = [self chatRealmWithFriend:friend];
    CKMessage *result;
    [realm beginWriteTransaction];
    result = [CKMessage createInRealm:realm withValue:msg];
    [realm commitWriteTransaction];
    [self.pendingQueue addObject:result];
    sendMsg(msg.roomAddress, [CKJsonParser serializeObject:dic]);
    return result;
}

-(RLMRealm*)chatRealmWithFriend:(CKFriend *)friend{
    NSString *path = [CKFriend chatRealmPathWithName:friend.address];
    for(RLMRealm *realm in self.chatRealCache){
        if([realm.path isEqualToString:path])
            return realm;}
    
    if(self.chatRealCache.count >= 10)
       [self.chatRealCache removeObjectAtIndex:0];
    NSData *key = [[NSData alloc] initWithBase64EncodedString:friend.encKey options:0];

    //RLMRealm *realm = [RLMRealm realmWithPath:path encryptionKey:key readOnly:NO error:NULL];
    RLMRealm *realm = [RLMRealm realmWithPath:path readOnly:NO error:NULL];
    [self.chatRealCache addObject:realm];
    return realm;
}

@end
