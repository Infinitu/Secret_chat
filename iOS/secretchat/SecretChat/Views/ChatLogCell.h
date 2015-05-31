//
//  ChatLogCellTableViewCell.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 8..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Message.h"
#define MAX_TIMESTAMP_SIZE 50

@interface ChatLogCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *contentsLabel;
@property (weak, nonatomic) IBOutlet UILabel *contentsLabelWrap;
@property (weak, nonatomic) IBOutlet UILabel *timestampLabel;
@property (weak, nonatomic) IBOutlet UIImageView *tail;

-(void)prepareView:(Message*)msg;
+(CGSize)guessTextSize:(NSString *)text withWidth:(CGFloat)width;
@end
