//
//  CKMessage.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Realm/Realm.h>

@interface CKMessage : RLMObject

@property NSString* text;
@property NSString* url;
@property NSString* type;
@property long datetime;
@property int idx;
@property NSString* roomAddress;
@property BOOL mine;

@end

// This protocol enables typed collections. i.e.:
// RLMArray<CKMessage>
RLM_ARRAY_TYPE(CKMessage)
