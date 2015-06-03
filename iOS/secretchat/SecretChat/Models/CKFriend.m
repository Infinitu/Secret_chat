//
//  CKFriend.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKFriend.h"

@implementation CKFriend

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
+(NSString*)chatRealmPathWithName:(NSString*)dbname {
    if (apppath == nil){
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
        apppath = [NSString stringWithFormat:@"%@/ChatDB", paths[0]];
        if(![[NSFileManager defaultManager] fileExistsAtPath:apppath isDirectory:nil])
            [[NSFileManager defaultManager] createDirectoryAtPath:apppath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return [NSString stringWithFormat:@"%@/%@.realm", apppath, dbname];
}
-(NSString*)chatRealmPath{
   return [CKFriend chatRealmPathWithName:self.address];
}



const NSString* key_address = @"address";
const NSString* key_nickname = @"nickname";
const NSString* key_profileImg =  @"profileImg";
const NSString* key_bloodType  = @"bloodType";
const NSString* key_age = @"age";
const NSString* key_sex =  @"sex";
const NSString* key_level = @"level";
const NSString* key_encKey = @"encKey";


+(CKFriend *)friendWithDictionary:(NSDictionary*)dictionary{
    CKFriend *res = [[CKFriend alloc]init];
    res.address = dictionary[key_address];
    if (res.address == nil) {
        return nil;
    }
    res.nickname    = dictionary[key_nickname];
    res.profileImg  = dictionary[key_profileImg];
    res.bloodType   = dictionary[key_bloodType];
    res.age         = [((NSNumber*) dictionary[key_age]) intValue];
    res.sex         = [((NSNumber*) dictionary[key_sex]) intValue];
    res.level       = dictionary[key_level];
    return res;
}

-(NSDictionary*)toDictionary{
    if(!self.profileImg) self.profileImg = @"";
    if(!self.level) self.level = @"";
    if(!self.encKey) self.encKey = @"";

    return @{
             key_address    : self.address,
             key_nickname   : self.nickname,
             key_profileImg : self.profileImg,
             key_bloodType  : self.bloodType,
             key_age        : @(self.age),
             key_sex        : @(self.sex),
             key_level      : self.level,
             key_encKey     : self.encKey
             };
}

- (BOOL)isRandom {
    return [self.address hasPrefix:@"random_"];
}


@end
