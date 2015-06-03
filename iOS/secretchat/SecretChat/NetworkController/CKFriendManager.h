//
//  CKFriendManager.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CKFriendManager : NSObject
+(CKFriendManager *)getInstance;
-(void)sendFriendRequestTo:(NSString*)address withMessage:(NSString*) message;
-(void)sendFriendResponseTo:(NSString*)address withAction:(NSString*) action;
-(void)messageReceivedFromMatchmaker:(NSDictionary *)message;

@end
