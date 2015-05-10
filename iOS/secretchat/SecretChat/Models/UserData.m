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
#define key_sysVersion   @"sysVersion"
#define key_devModel     @"devModel"
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
    ud.sysVersion   = [userDefault stringForKey:key_sysVersion];
    ud.devModel     = [userDefault stringForKey:key_devModel];
    ud.profile      = [Friend friendWithDictionary:[userDefault dictionaryForKey:key_profile]];
    return ud;
}
-(void)saveToUserDefault:(NSUserDefaults*)userDefault{
    [userDefault setValue:self.accessToken  forKey:key_accessToken];
    [userDefault setValue:self.deviceId     forKey:key_deviceId];
    [userDefault setValue:self.sysVersion   forKey:key_sysVersion];
    [userDefault setValue:self.devModel     forKey:key_devModel];
    [userDefault setValue:self.profile      forKey:key_profile];
}

@end