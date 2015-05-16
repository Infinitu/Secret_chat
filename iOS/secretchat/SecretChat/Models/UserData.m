//
//  UserData.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 10..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "UserData.h"
#define key_accessToken  @"accessToken"
#define key_deviceId     @"deviceId"
#define key_profile      @"profile"


@interface UserData ()

@end
@implementation UserData

+(UserData*)userDataFromUserDefault:(NSUserDefaults*)userDefault{
    UserData *ud = [[UserData alloc]init];
    ud.accessToken  = [userDefault stringForKey:key_accessToken];
    if(ud.accessToken == nil)
        return nil;
    ud.deviceId     = [userDefault stringForKey:key_deviceId];
    ud.profile      = [Friend friendWithDictionary:[userDefault dictionaryForKey:key_profile]];
    return ud;
}
-(void)saveToUserDefault:(NSUserDefaults*)userDefault{
    [userDefault setValue:self.accessToken  forKey:key_accessToken];
    [userDefault setValue:self.deviceId     forKey:key_deviceId];
    [userDefault setValue:[self.profile toDictionary]      forKey:key_profile];
    [userDefault synchronize];
}

@end