//
//  CKUserData.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 5..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CKFriend.h"

@interface CKUserData : NSObject

@property NSString* accessToken;
@property NSString* deviceId;
@property CKFriend *   profile;

+(CKUserData *)userDataFromUserDefault:(NSUserDefaults*)userDefault;
-(void)saveToUserDefault:(NSUserDefaults*)userDefault;

@end
