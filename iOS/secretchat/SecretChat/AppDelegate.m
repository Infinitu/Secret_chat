//
//  AppDelegate.m
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "AppDelegate.h"
#import "DetailViewController.h"
#import "NetworkManager.h"
#import "Version.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    self.userData = [UserData userDataFromUserDefault:defaults];
    if(self.userData == nil){
        //todo go regirataion storyboard.
        self.userData = [[UserData alloc]init];
        UIDevice* device = [UIDevice currentDevice];
        self.userData.accessToken = @"abcdefg";
        self.userData.deviceId = [[device identifierForVendor] UUIDString];
        self.userData.profile = [Friend objectForPrimaryKey:@"554f5491fc3ab59ae969050e"];
        
        if(self.userData.profile == nil){
            Friend *fr = [[Friend alloc]init];
            fr.address = @"554f5491fc3ab59ae969050e";
            fr.nickname = @"ECHO";
            fr.profileImg = @"http://graph.facebook.com/glenn.c.kim/picture";
            fr.age = 22;
            fr.sex = 1;
            fr.bloodType = @"A";
            fr.level = @"silver-2";
            fr.encKey = @"YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ==";
            
            RLMRealm *realm = [RLMRealm defaultRealm];
            [realm beginWriteTransaction];
            [Friend createInDefaultRealmWithObject:fr];
            [realm commitWriteTransaction];
            self.userData.profile = fr;
        }
//        [self.userData saveToUserDefault:defaults];
    }
    
    
    [[NSBundle mainBundle] resourcePath];
//    NSSearchPathForDirectoriesInDomains(NSCachesDirectory, <#NSSearchPathDomainMask domainMask#>, <#BOOL expandTilde#>
//    
//    NSFileManager *fm = [NSFileManager defaultManager];
//    NSString *path = [NSString stringWithFormat:@"%@/Library/Caches/ChatDB",[[ mainBundle]  ofType:@"realm"]];
//    if(![fm fileExistsAtPath:path isDirectory:nil])
//        [fm createDirectoryAtPath:path withIntermediateDirectories:NO attributes:nil error:nil];
    
    [NetworkManager initializeWithUserData:self.userData withHost:DEFAULT_HOST withPort:DEFAULT_PORT];

    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
