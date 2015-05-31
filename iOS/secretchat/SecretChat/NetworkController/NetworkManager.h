//
//  NetworkManager.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UserData.h"

#define STATUS_CHANGED_OPENED @"SOCKET_STATUS_OPENED"
#define STATUS_CHANGED_CLOSED @"SOCKET_STATUS_CLOSED"
#define STATUS_CHANGED_AUTH_FAILED @"SOCKET_STATUS_AUTH_FAILED"

@interface NetworkManager : NSObject
+(NetworkManager *)getInstance;
-(BOOL)socketInitializeWithUserData:(UserData *)userData withHost:(NSString *)serverHost withPort:(UInt32)port;
-(void)finalizeNetwork;
@property(readonly) NSString* host;
@property(readonly) UInt32 port;
@end
