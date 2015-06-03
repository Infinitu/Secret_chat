//
//  CKFriendRequest.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Realm/Realm.h>

@interface CKFriendRequest : RLMObject
@property NSString *address;
@property NSString *nickname;
@property NSString *profileImage;
@property NSString *message;
@property NSString *status;
@end

// This protocol enables typed collections. i.e.:
// RLMArray<CKFriendRequest>
RLM_ARRAY_TYPE(CKFriendRequest)
