//
//  AppDelegate.m
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "AppDelegate.h"
#import "CKChatRoomController.h"
#import "CKNetworkManager.h"
#import "Version.h"
#import "CKNotificationManager.h"
#import "CKSampleDataGenerator.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSLog(@"%@",[RLMRealm defaultRealmPath]);
    _status = 2;
    sleep(1);
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    self.userData = [CKUserData userDataFromUserDefault:defaults];

    UIUserNotificationType types = UIUserNotificationTypeBadge |
            UIUserNotificationTypeSound | UIUserNotificationTypeAlert;

    UIUserNotificationSettings *mySettings =
            [UIUserNotificationSettings settingsForTypes:types categories:nil];

    [[UIApplication sharedApplication] registerUserNotificationSettings:mySettings];
    [[UIApplication sharedApplication] registerForRemoteNotifications];
    

    if(self.userData == nil&&false){
        UIStoryboard *regiBoard = [UIStoryboard storyboardWithName:@"Registration" bundle:nil];
        self.window.rootViewController = [regiBoard instantiateInitialViewController];
        [self.window makeKeyAndVisible];
        return YES;
    }
    _status = 3;
    [self initializeWithUserData:self.userData];
    [[CKSampleDataGenerator getInstance] cpySampleFriendsListFromBundle:[NSBundle mainBundle]];
    return YES;
}

-(void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken{
    [[CKNotificationManager getInstance] application:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}
- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error{
    [[CKNotificationManager getInstance] application:application didFailToRegisterForRemoteNotificationsWithError:error];
}

-(void)initializeWithUserData:(CKUserData *)ud{
    self.userData=ud;
//    [[CKNetworkManager getInstance]socketInitializeWithUserData:ud withHost:DEFAULT_HOST withPort:DEFAULT_PORT];
    UIStoryboard *regiBoard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    self.window.rootViewController = [regiBoard instantiateInitialViewController];
}

- (BOOL)isInBackground {
    NSLog(@"count%d ,%d",_status,(BOOL)_status&2);
    return !((BOOL) (_status&2));
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    [[UIApplication sharedApplication] setKeepAliveTimeout:36000 handler:nil];
    _status = _status & 1;
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.

}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    _status = _status | 2;
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
//    [[CKNetworkManager getInstance] finalizeNetwork];
}

@end
