//
//  NotificationManager.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 31..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface NotificationManager : NSObject

@property(readonly) NSString* deviceId;

+(instancetype)getInstance;
-(void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)token;

- (void)pushNotification:(NSString*)body withTitle:(NSString*)title withAction:(NSString*)action withUserInfo:(NSDictionary*)info;

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
-(void)resetBadgeCnt:(NSInteger)cnt;
-(void)addBadgeCnt:(NSInteger)cnt;
@end
