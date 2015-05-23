//
//  FriendRequestTableViewCell.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "FriendRequestTableViewCell.h"

@implementation FriendRequestTableViewCell

- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
- (IBAction)acceptButtonClicked:(id)sender {
    [self.caller AcceptFriends:self.req];
}

@end
