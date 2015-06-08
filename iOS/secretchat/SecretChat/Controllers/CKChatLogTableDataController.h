//
//  ChatLogTableDataController.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 8..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CKFriend.h"
#import "CKChatLogCell.h"

@interface CKChatLogTableDataController : NSObject <UITableViewDataSource,UITableViewDelegate>

@property NSMutableArray *objects;
@property NSMutableArray *pendingObjects;
@property(readonly) BOOL isScrollFollwing;
@property(readonly) RLMRealm *realm;
-(CKChatLogTableDataController*)initWithRealm:(RLMRealm*)realm;
-(NSIndexPath*)last;
-(void)updateScroll:(UIScrollView*)scroll;
-(void)reloadAllMessages;
@end
