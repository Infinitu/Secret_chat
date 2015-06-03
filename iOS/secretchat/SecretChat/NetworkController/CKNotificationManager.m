//
//  CKNotificationManager.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 31..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKNotificationManager.h"
CKNotificationManager *notimanager_instance;

@interface CKNotificationManager ()
@property NSInteger badge_cnt;
@end

@implementation CKNotificationManager
+(instancetype)getInstance{
    if(!notimanager_instance)
        notimanager_instance = [[CKNotificationManager alloc]init];
    return notimanager_instance;
}

-(void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)token{
    NSMutableString *str;

    str = [NSMutableString stringWithCapacity:token.length*2+6];
    [str appendString:@"apns::"];
    uint8_t *bytes = (uint8_t*)[token bytes];
    for(int i =0; i<token.length;i++)
        [str appendFormat:@"%02x",bytes[i]];
    _deviceId = str;
    NSLog(@"deviceID is %@",_deviceId);
}


- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error{
    NSLog(@"failed To Register Remote Notification.\n%@",error);
}

- (void)pushNotification:(NSString*)body withTitle:(NSString*)title withAction:(NSString*)action withUserInfo:(NSDictionary*)info{
    UILocalNotification *localNotif = [[UILocalNotification alloc] init];
    localNotif.alertBody = body;
    localNotif.alertAction = action;
    localNotif.alertTitle = title;
    
    localNotif.soundName = UILocalNotificationDefaultSoundName;
    localNotif.applicationIconBadgeNumber = self.badge_cnt;
    localNotif.userInfo = info;
    
    [[UIApplication sharedApplication] scheduleLocalNotification:localNotif];
}

-(void)resetBadgeCnt:(NSInteger)cnt{
    self.badge_cnt = cnt;
}

-(void)addBadgeCnt:(NSInteger)cnt{
    self.badge_cnt+=cnt;
    [self pushNotification:nil withTitle:nil withAction:nil withUserInfo:nil];
}

@end
