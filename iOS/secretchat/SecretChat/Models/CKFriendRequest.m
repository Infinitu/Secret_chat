//
//  CKFriendRequest.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKFriendRequest.h"

@implementation CKFriendRequest

+ (NSString *)primaryKey{
    return @"address";
}

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

@end
