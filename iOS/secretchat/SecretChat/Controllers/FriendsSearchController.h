//
// Created by 김창규 on 15. 5. 20..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FriendRequest.h"

@interface FriendsSearchController : UIViewController<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>
-(void)AcceptFriends:(FriendRequest *)req;
-(void)DenyFriends:(FriendRequest *)req;
- (void)prepareForNavigationBar:(UINavigationItem *)navi;
@end