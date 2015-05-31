//
//  FriendsCollectionViewCell.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 16..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Friend.h"

@interface FriendTableCell : UICollectionViewCell
@property (weak, nonatomic) UIImageView *profileImageView;
@property (weak, nonatomic) UILabel *nicknameLabel;
@property (weak, nonatomic) Friend *friend;
@end
