//
//  CKChatRoomController.h
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#define CURRENT_SYSTEM_TIME_MILLIS_NOW ([[NSDate date] timeIntervalSince1970]*1000)

@interface CKChatRoomController : UIViewController

@property (strong, nonatomic) id detailItem;

@end

