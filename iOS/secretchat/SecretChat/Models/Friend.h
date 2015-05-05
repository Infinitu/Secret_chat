//
//  Friend.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Realm/Realm.h>

@interface Friend : RLMObject
@property NSString* address;
@property NSString* nickname;
@property NSString* profileImg;
@property NSString* bloodType;
@property int   age;
@property int   sex;
@property NSString* level;

@end

// This protocol enables typed collections. i.e.:
// RLMArray<Friends>
RLM_ARRAY_TYPE(Friends)
