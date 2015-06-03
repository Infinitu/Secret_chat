//
// Created by 김창규 on 15. 5. 31..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import "CKRandomChatManager.h"


@implementation CKRandomChatManager
CKRandomChatManager *randomchat_instance;

+(instancetype)getInstance{
    if(!randomchat_instance)
        randomchat_instance = [[CKRandomChatManager alloc] init];
    return randomchat_instance;
}

-(void)enqueueSuccessfully{
    self.isInQueue = TRUE;
}

-(void)dequeueSuccessfully{
    self.isInQueue = FALSE;
}

-(void)matchEstablished:(NSDictionary*)dictionary{
    self.isInQueue = FALSE;
}
-(void)failed:(NSDictionary*)dictionary{}
@end