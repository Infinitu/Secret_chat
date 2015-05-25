//
//  NetworkManager.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "NetworkManager.h"
#import "ProtocolSocket.h"
#import "MessageDispatcher.h"
#import "Version.h"

void sendMissingMessageRequest();

@implementation NetworkManager

UserData *myself = nil;
bool waiting_pong;
enum socket_status status=DISCONNECTED;

+(void)initializeWithUserData:(UserData*)userData withHost:(NSString*)serverhost withPort:(NSInteger)port{
    socket_init(serverhost,port);
    myself = userData;
//    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
}

void setStatus(enum socket_status newStatus){
    status = newStatus;
    statusChanged(newStatus);
}

void messageComplete(int header, CFDictionaryRef cfDictionary){
    NSDictionary *dictionary = (__bridge NSDictionary*)cfDictionary;
    int64_t datetime;
    switch (header) {
        case 0x0001: // ping
            sendPong();
            break;
        case 0x0002: // pong
            waiting_pong = false;
            break;
        case 0x1002: // session okay
            setStatus(AUTHORIZED);
            break;
        case 0x1003: // redirect server
            //todo redirect
            break;
        case 0x1004: // auth failed
            break;
        case 0x1005: // internal server err
            break;
        case 0x1006: // banned user.
            break;
        case 0x1007: // client version is not permited.
            break;
        case 0x1101: // disconnected by another connection.
            break;
        case 0x2012: // seding message successful.
            datetime = ((NSString*) dictionary[(__bridge NSString *) KEY_SEND_DATETIME]).longLongValue;
            [[MessageDispatcher getInstance] sendSuccess:(long) datetime];
            break;
        case 0x2013: // seding message failed.
            //todo Error Handling
            break;
        case 0x2101: // new message arrival
            [[MessageDispatcher getInstance] newChatMessage:dictionary];
            break;
        case 0x2102: // new chunked message arrival begin
            //tod
            break;
        case 0x2103: // new chunked message arrival continue
            //todo
            break;
        case 0x2104: // new chunked message arrival end
            //todo
            break;
        case 0x3001: // missing message notification.
            [[NSNotificationCenter defaultCenter]
                    postNotificationName:@"MissedMessageNotification"
                                  object:nil
                                userInfo:@{ @"count":
                                        @([(NSString*)dictionary[(__bridge NSString *)KEY_MSG_CNT] integerValue])}];
            sendMissingMessageRequest();
            break;
        case 0x3003: // missing message
            [[MessageDispatcher getInstance] newChatMessage:dictionary];
            break;
        case 0x3102: // message read check.
            //todo
            break;
        case 0x4101: // enqueue successful.
            //todo
            break;
        case 0x4111: // enqueue failed : already in queue
            //todo
            break;
        case 0x4112: // enqueue failed : match count limit excessed.
            //todo
            break;
        case 0x4201: // match established
            //todo
            break;
        case 0x4202: // match timeout
            //todo
            break;
        case 0x4302: // friends request in random room.
            //todo
            break;
    }
}

void socketOpened(){
    setStatus(CONNECTED);
    sendAuth();
}
void socketClosed(){
    
}
void socketError(){
    
}

void pingTimeout(){
    //todo disconnect and reconnect
}

void statusChanged(enum socket_status status){
    switch (status){
        case AUTHORIZED:
            break;
        default :
            break;
    }
}

void sendPing(){
    uint8_t nullstr = '\0';
    sendMessage(0x0001,&nullstr);
    waiting_pong = true;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(PING_TIMEOUT_IN_SEC * NSEC_PER_SEC)),
                   dispatch_get_main_queue(),
                   ^{
                       if (waiting_pong) {
                           waiting_pong = false;
                           pingTimeout();
                       }
                   });
}

void sendPong(){
    uint8_t nullstr = '\0';
    sendMessage(0x0002,&nullstr);
}

void sendAuth(){
    UIDevice* device = [UIDevice currentDevice];
    sendMessage(0x1001,(uint8_t*)([[NSString stringWithFormat:@"%@|%@|%@|%@|%@|%@|",
                                    PROTOCOL_VERSION,
                                    myself.deviceId,
                                    myself.accessToken,
                                    device.systemVersion,
                                    APP_VERSION,
                                    device.model]
                                   cStringUsingEncoding:NSUTF8StringEncoding]));

}

void receiveSuccessfully(NSString* sender, long datetime, int idx){
    sendMessage(0x2111, (uint8_t*)[[NSString stringWithFormat:@"%@|%ld|%d|", sender, datetime, idx]
                                   cStringUsingEncoding:NSUTF8StringEncoding]);
}

void sendMsg(NSString* address, NSString* msg){
    sendMessage(0x2001,(uint8_t*)([[NSString stringWithFormat:@"%@|%@|",
                                    address, msg]
                                    cStringUsingEncoding:NSUTF8StringEncoding]));
}


void sendMissingMessageRequest() {
    uint8_t nullstr = '\0';
    sendMessage(0x3002,&nullstr);
}

@end

