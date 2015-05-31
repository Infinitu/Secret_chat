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
#import "NotificationManager.h"
#import "RandomChatManager.h"

void sendMissingMessageRequest();
void sendPing();

void sendAuth();

@interface NetworkManager ()

@property NSTimer *ticker;
@property NSNotificationCenter *notificationCenter;
@end

@implementation NetworkManager
NetworkManager *networkmanager_instance;


bool waiting_pong=false;
UserData *myself;
enum socket_status status=DISCONNECTED;

+(NetworkManager *)getInstance {
    if(!networkmanager_instance){
        networkmanager_instance = [[NetworkManager alloc] init];
    }
    return networkmanager_instance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _notificationCenter = [NSNotificationCenter defaultCenter];
    }

    return self;
}

-(BOOL)socketInitializeWithUserData:(UserData *)userData withHost:(NSString *)serverHost withPort:(UInt32)port{
    [self finalizeNetwork];
    if(!userData)
        return NO;
    _host = serverHost;
    _port = port;
    myself = userData;
    [self initializeNetwork];
    return YES;
}


//Network Status management

-(void)finalizeNetwork{
    if(self.ticker){
        [self.ticker invalidate];
    }
    waiting_pong = false;
    socket_finalize();
    socketClosed();
}

-(void)initializeNetwork{
    if(!self.ticker)
        _ticker = [NSTimer timerWithTimeInterval:PING_INTERVAL
                                          target:self
                                        selector:@selector(sendTick)
                                        userInfo:nil
                                         repeats:YES];
    [self setStatus:CONNECTING];
    socket_init((__bridge CFStringRef) self.host, self.port);
}
-(void)redirectSocket:(NSDictionary*)dictionary{
    _host = dictionary[(__bridge NSString *) KEY_REDIRECT_SERVER_HOST];
    _port = [dictionary[(__bridge NSString *) KEY_REDIRECT_SERVER_HOST] unsignedIntValue];
    NSLog(@"redirecting to %@:%lu",_host,_port);
    socket_finalize();
    socket_init((__bridge CFStringRef) _host, _port);
}


-(void)sendTick{
    [self sendPing];
}

-(void)setStatus:(enum socket_status)newStatus{
    if(status == newStatus)
        return;
    status = newStatus;
    [self statusChanged:newStatus];
}

void pingTimeout(){
    [[NetworkManager getInstance] finalizeNetwork];
}

void socketOpened(){
    [[NetworkManager getInstance]setStatus:CONNECTED];
    sendAuth();
}
void socketClosed(){
    [[NetworkManager getInstance]setStatus:CLOSED];
}
void socketError(){
    socket_finalize();
    socketClosed();
}

void sendingException(NSException *exception){
    NSLog(@"sending excepption :: %@",exception);
    //todo anothor error handing.
}

-(void)statusChanged:(enum socket_status)status{
    switch (status){
        case CONNECTED:
            [[NSRunLoop currentRunLoop] addTimer:self.ticker forMode:NSRunLoopCommonModes];
            break;
        case AUTHORIZED:
            [self.notificationCenter postNotificationName:STATUS_CHANGED_OPENED object:self];
            break;
        case CLOSED:
            [self.notificationCenter postNotificationName:STATUS_CHANGED_CLOSED object:self];
        default :
            break;
    }
}




//message receive
void messageComplete(int header, CFDictionaryRef cfDictionary){
    NSDictionary *dictionary = (__bridge NSDictionary*)cfDictionary;
    NSInteger cnt;
    int64_t datetime;
    switch (header) {
        case 0x0001: // ping
            sendPong();
            break;
        case 0x0002: // pong
            waiting_pong = false;
            break;
        case 0x1002: // session okay
            [[NetworkManager getInstance]setStatus:AUTHORIZED];
            break;
        case 0x1003: // redirect server
            //todo redirect
            break;
        case 0x1004: // auth failed
            [networkmanager_instance.notificationCenter postNotificationName:STATUS_CHANGED_AUTH_FAILED
                                                                      object:networkmanager_instance
                                                                    userInfo:dictionary];
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
            cnt =  [(NSString*)dictionary[(__bridge NSString *)KEY_MSG_CNT] integerValue];
            [[NSNotificationCenter defaultCenter]
                    postNotificationName:@"MissedMessageNotification"
                                  object:nil
                                userInfo:@{ @"count":@(cnt)}];
            [[NotificationManager getInstance] addBadgeCnt:cnt];
            sendMissingMessageRequest();
            break;
        case 0x3003: // missing message
            [[MessageDispatcher getInstance] newChatMessage:dictionary];
            break;
        case 0x3102: // message read check.
            //todo
            break;
        case 0x4101: // enqueue successful.
            [[RandomChatManager getInstance] enqueueSuccessfully];
            break;
        case 0x4111: // enqueue failed : already in queue
        case 0x4112: // enqueue failed : match count limit excessed.
            [[RandomChatManager getInstance] failed:dictionary];
            break;
        case 0x4201: // match established
            [[RandomChatManager getInstance] matchEstablished:dictionary];
            break;
        case 0x4202: // match timeout
            [[RandomChatManager getInstance] failed:dictionary];
            break;
        case 0x4302: // friends request in random room.
            //todo
            break;
    }
}

//actions
void sendMessageWithErrorHandling(int header, uint8_t * body){
    @try {
        sendMessage(header, body);
    }
    @catch(NSException *exception){
        sendingException(exception);
    }
}

-(BOOL)sendPing{
    uint8_t nullstr = '\0';
    sendMessage(0x0001,&nullstr);
    if(waiting_pong)return false;
    waiting_pong = true;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(PING_TIMEOUT_IN_SEC * NSEC_PER_SEC)),
                   dispatch_get_main_queue(),
                   ^{
                       if (waiting_pong) {
                           waiting_pong = false;
                           pingTimeout();
                       }
                   });
    return true;
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

void randomEnqueu(){
    uint8_t nullstr = '\0';
    sendMessage(0x4001,&nullstr);
}

void randomDequeu(){
    uint8_t nullstr = '\0';
    sendMessage(0x4002,&nullstr);
}

@end

