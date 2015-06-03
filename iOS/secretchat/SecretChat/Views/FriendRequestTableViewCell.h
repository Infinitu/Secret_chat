//
//  FriendRequestTableViewCell.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FriendsSearchController.h"

@interface FriendRequestTableViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UIImageView *profileImageView;
@property (weak, nonatomic) IBOutlet UILabel *nickname;

@property (weak, nonatomic) FriendsSearchController *caller;
@property (weak, nonatomic) FriendRequest *req;
@end
