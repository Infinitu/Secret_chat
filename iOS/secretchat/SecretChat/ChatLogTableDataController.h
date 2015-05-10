//
//  ChatLogTableDataController.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 8..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Friend.h"
#import "ChatLogCell.h"

@interface ChatLogTableDataController : NSObject <UITableViewDataSource,UITableViewDelegate>

@property NSMutableArray *objects;
@property NSMutableArray *pendingObjects;

-(ChatLogTableDataController*)initWithRealm:(RLMRealm*)realm;
-(NSIndexPath*)last;
@end
