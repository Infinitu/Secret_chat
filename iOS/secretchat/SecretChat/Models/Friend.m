//
//  Friend.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "Friend.h"

@implementation Friend

// Specify default values for properties

//+ (NSDictionary *)defaultPropertyValues
//{
//    return @{};
//}

// Specify properties to ignore (Realm won't persist these)

//+ (NSArray *)ignoredProperties
//{
//    return @[];
//}

+ (NSString *)primaryKey{
    return @"address";
}

NSString *apppath = nil;
+(NSString*)chatRealmPath:(NSString*)address{
    if (apppath == nil){
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
        apppath = [NSString stringWithFormat:@"%@/ChatDB",[paths objectAtIndex:0]];
        if(![[NSFileManager defaultManager] fileExistsAtPath:apppath isDirectory:nil])
            [[NSFileManager defaultManager] createDirectoryAtPath:apppath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [NSString stringWithFormat:@"%@/%@.realm",apppath,address];
}
-(NSString*)chatRealmPath{
   return [Friend chatRealmPath:self.address];
}
const NSString* key_address = @"address";
const NSString* key_nickname = @"nickname";
const NSString* key_profileImg =  @"profileImg";
const NSString* key_bloodType  = @"bloodType";
const NSString* key_age = @"age";
const NSString* key_sex =  @"sex";
const NSString* key_level = @"level";
const NSString* key_encKey = @"encKey";


+(Friend*)friendWithDictionary:(NSDictionary*)dictionary{
    Friend *res = [[Friend alloc]init];
    res.address = [dictionary objectForKey:key_address];
    if (res.address == nil) {
        return nil;
    }
    res.nickname = [dictionary objectForKey:key_nickname];
    res.profileImg = [dictionary objectForKey:key_profileImg];
    res.bloodType = [dictionary objectForKey:key_bloodType];
    res.age     = [((NSNumber*)[dictionary objectForKey:key_age]) intValue];
    res.sex     = [((NSNumber*)[dictionary objectForKey:key_sex]) intValue];
    res.level = [dictionary objectForKey:key_level];
    return res;
}

-(NSDictionary*)toDictionary{
    return @{
             key_address    : self.address,
             key_nickname   : self.nickname,
             key_profileImg : self.profileImg,
             key_bloodType  : self.bloodType,
             key_age        : [NSNumber numberWithInt:self.age],
             key_sex        : [NSNumber numberWithInt:self.sex],
             key_level      : self.level,
             key_encKey     : self.encKey,
             };
}

@end
