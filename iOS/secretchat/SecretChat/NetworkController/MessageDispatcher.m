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
#define RLM_CACHE_CNT 10;

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
    NSDictionary *msgJson = [CKJsonParser parseJson:[dictionary objectForKey:(NSString*)KEY_MESSAGE_JSON]];
    Message *msg =[[Message alloc]init];
    msg.text        = [msgJson objectForKey:@"text"];
    msg.url         = [msgJson objectForKey:@"url"];
    msg.type        = [msgJson objectForKey:@"type"];
    msg.datetime    = [[dictionary objectForKey:(NSString*)KEY_SEND_DATETIME] longLongValue];
    msg.idx         = [[dictionary objectForKey:(NSString*)KEY_INDEX] intValue];
    msg.roomAddress = [dictionary objectForKey:(NSString*)KEY_ADDRESS];
    msg.mine        = false;

    RLMRealm *realm = [self chatRealmWithAddress:msg.roomAddress];
    Message *result;
    [realm beginWriteTransaction];
    result = [Message createInRealm:realm withObject:msg];
    [realm commitWriteTransaction];
    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":result}];
    receiveSuccessfully(msg.roomAddress, msg.datetime, msg.idx);
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
    result = [Message createInRealm:realm withObject:msg];
    [realm commitWriteTransaction];
    [self.pendingQueue addObject:result];
    sendMsg(msg.roomAddress, [CKJsonParser serializeObject:dic]);
    return result;
}
//-(void)sendMessageType:(NSString*)type
//           withMessage:(NSString*)text
//               withUrl:(NSString*)url
//             toAddress:(NSString*)address{
//
//    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
//    [dic setObject:type forKey:@"type"];
//    if(text)
//        [dic setObject:text forKey:@"text"];
//    if(url)
//        [dic setObject:url forKey:@"url"];
//
//    Message *msg =[[Message alloc]init];
//    msg.text        = text;
//    msg.url         = url;
//    msg.type        = type;
//    msg.datetime    = -1;
//    msg.idx         = -1;
//    msg.roomAddress = address;
//    msg.mine        = true;
//    RLMRealm *realm = [self chatRealmWithAddress:msg.roomAddress];
//    [realm beginWriteTransaction];
//    [Message createInRealm:realm withObject:msg];
//    [realm commitWriteTransaction];
//    [self.notiCenter postNotificationName:msg.roomAddress object:self userInfo:@{@"msg":msg}];
//    [self.pendingQueue addObject:msg];
//
//    sendMsg(address, [CKJsonParser serializeObject:dic]);
//}
//
//-(Message*)sendMesssageURL:(NSString*)url toFriend:(Friend*)friend{
//    return [self sendMessageType:@"url" withMessage:nil withUrl:url toAddress:friend.address];
//}
//
//-(Message*)sendMesssageText:(NSString*)message toFriend:(Friend*)friend{
//    return [self sendMessageType:@"text" withMessage:message withUrl:nil toAddress:friend.address];
//}

-(RLMRealm*)chatRealmWithAddress:(NSString*)address{
    NSString *path =[Friend chatRealmPath:address];
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
