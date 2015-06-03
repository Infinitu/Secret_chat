//
//  CKMessageDispatcher.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "CKFriend.h"
#import "CKMessage.h"

@interface CKMessageDispatcher : NSObject
+(CKMessageDispatcher *)getInstance;
-(void)sendSuccess:(long)datetime;
-(void)newChatMessage:(NSDictionary*)dictionary;
//-(CKMessage*)sendMesssageText:(NSString*)message toFriend:(CKFriend*)friend;
-(CKMessage *)sendMessage:(CKMessage *)msg toFriend:(CKFriend *)friend;
-(RLMRealm*)chatRealmWithFriend:(CKFriend *)friend;
void sendMsg(NSString* address, NSString* msg);
void receiveSuccessfully(NSString* sender, long datetime, int idx);
@end
