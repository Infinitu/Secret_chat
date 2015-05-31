//
//  FriendsCollectionViewCell.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 16..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "FriendsTableCell.h"

@implementation FriendsTableCell
-(instancetype)init{
    self = [super init];
    if(!self) return self;

    [self initSubviews];


    return self;
}

- (void)awakeFromNib {
    [self initSubviews];
}

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        [self initSubviews];
    }

    return self;
}

- (NSString *)reuseIdentifier {
    return @"friend_cell";
}


- (void)initSubviews {
    _timesLabel = [[UILabel alloc] init];
    _nicknameLabel = [[UILabel alloc] init];
    _profileImageView = [[UIImageView alloc] init];

    _profileImageView.frame = CGRectMake(16, 16, 45, 45);
    _profileImageView.layer.cornerRadius = 22.5;
    _profileImageView.layer.masksToBounds = YES;
    _nicknameLabel.frame = CGRectMake(73, 16, 220, 30);
    _timesLabel.frame = CGRectMake(73, 40, 220, 21);

    UIFont *font1 = _nicknameLabel.font;
    _nicknameLabel.font = [font1 fontWithSize:17];
    _timesLabel.font = [font1 fontWithSize:15];
    _timesLabel.textColor = [UIColor
            colorWithRed: (CGFloat)(0x8E)/0xFF
                   green: (CGFloat)(0x8E)/0xFF
                    blue: (CGFloat)(0x93)/0xFF alpha:1];
    _timesLabel.text = @"7분전";
    self.accessoryType = UITableViewCellAccessoryDisclosureIndicator;


    [self addSubview:_timesLabel];
    [self addSubview:_nicknameLabel];
    [self addSubview:_profileImageView];
}

@end
