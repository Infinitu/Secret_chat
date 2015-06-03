//
//  AppDelegate.h
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CKUserData.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>
@property(readonly) int status; //0 not logedin background 1 logedin background
                                // 2 foreground notlogedin 3 logedin foreground

@property (strong, nonatomic) UIWindow *window;
@property CKUserData *userData;

-(void)initializeWithUserData:(CKUserData *)ud;
-(BOOL)isInBackground;
- (void)pushNotification:(NSString*)body withTitle:(NSString*)title withAction:(NSString*)action withUserInfo:(NSDictionary*)info;

@end

