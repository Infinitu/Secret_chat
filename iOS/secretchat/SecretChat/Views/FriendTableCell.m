//
//  FriendsCollectionViewCell.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 16..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "FriendTableCell.h"

@implementation FriendTableCell
-(void)awakeFromNib{
    _nicknameLabel = (UILabel *) [self viewWithTag:2];
    _profileImageView = (UIImageView *) [self viewWithTag:1];
}
@end
