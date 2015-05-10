//
//  UserData.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 5..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Friend.h"

@interface UserData : NSObject

@property NSString* accessToken;
@property NSString* deviceId;
@property NSString* sysVersion;
@property NSString* devModel;
@property Friend*   profile;

+(UserData*)userDataFromUserDefault:(NSUserDefaults*)userDefault;
-(void)saveToUserDefault:(NSUserDefaults*)userDefault;

@end
