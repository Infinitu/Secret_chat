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
    Message *msg = [self.pendingQueue objectAtIndex:0];
    [self.pendingQueue removeObjectAtIndex:0];
    if(lastDT==datetime)
        lastDTCnt ++;
    else{
        lastDTCnt = 0;
        lastDT = datetime;
    }
    RLMRealm *realm = [self chatRealmWithAddress:msg.roomAddress];
    [realm beginWriteTransaction];
    msg.datetime = datetime;
    msg.idx = lastDTCnt;
    [realm commitWriteTransaction];
    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":msg}];
}

-(void)newChatMessage:(NSDictionary*)dictionary{
    NSLog(@"%@",dictionary);
    NSString *address = dictionary[(__bridge NSString *) KEY_ADDRESS];
    if([address containsString:@"system_"])
        return [self newSystemMessage:address withData:dictionary];
    NSDictionary *msgJson = [CKJsonParser parseJson:[dictionary objectForKey:(NSString*)KEY_MESSAGE_JSON]]; //todo Decrypt
    Message *msg =[[Message alloc]init];
    msg.text        = [msgJson objectForKey:@"text"];
    msg.url         = [msgJson objectForKey:@"url"];
    msg.type        = [msgJson objectForKey:@"type"];
    msg.datetime    = [[dictionary objectForKey:(NSString*)KEY_SEND_DATETIME] longLongValue];
    msg.idx         = [[dictionary objectForKey:(NSString*)KEY_INDEX] intValue];
    msg.roomAddress = address;
    msg.mine        = false;

    RLMRealm *realm = [self chatRealmWithAddress:msg.roomAddress];
    Message *result;
    [realm beginWriteTransaction];
    result = [Message createInRealm:realm withValue:msg];
    [realm commitWriteTransaction];
    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":result}];
    receiveSuccessfully(msg.roomAddress, msg.datetime, msg.idx);
}

-(void)newSystemMessage:(NSString*)systemUserAddr withData:(NSDictionary *)dictionary{
    long dt = (long) [dictionary[(__bridge NSString *) KEY_SEND_DATETIME] longLongValue];
    int idx = [dictionary[(__bridge NSString *) KEY_INDEX] intValue];
    if([systemUserAddr isEqualToString:@"system_matchmaker"])
        [[FriendManager getInstance] messageReceivedFromMatchmaker:dictionary[(__bridge NSString *)KEY_MESSAGE_JSON]];
    receiveSuccessfully(systemUserAddr,dt,idx);
}

-(Message*)sendMessage:(Message*)msg{
    
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    [dic setObject:msg.type forKey:@"type"];
    if(msg.text)
        [dic setObject:msg.text forKey:@"text"];
    if(msg.url)
        [dic setObject:msg.url forKey:@"url"];

    RLMRealm *realm = [self chatRealmWithAddress:msg.roomAddress];
    Message *result;
    [realm beginWriteTransaction];
    result = [Message createInRealm:realm withValue:msg];
    [realm commitWriteTransaction];
    [self.pendingQueue addObject:result];
    sendMsg(msg.roomAddress, [CKJsonParser serializeObject:dic]);
    return result;
}

-(RLMRealm*)chatRealmWithAddress:(NSString*)address{
    NSString *path = [Friend chatRealmPathWithName:address];
    for(RLMRealm *realm in self.chatRealCache)
        if([realm.path isEqualToString:path])
            return realm;
    
    if(self.chatRealCache.count >= 10)
       [self.chatRealCache removeObjectAtIndex:0];

    Friend *friend = [Friend objectForPrimaryKey:address];
    NSData *key = [[NSData alloc] initWithBase64EncodedString:friend.encKey options:0];

    RLMRealm *realm = [RLMRealm realmWithPath:path encryptionKey:key readOnly:NO error:NULL];
    
    [self.chatRealCache addObject:realm];
    return realm;
}

@end
