//
// Created by 김창규 on 15. 5. 20..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CKFriendRequest.h"

@interface CKFriendRequestController : UIViewController<UITableViewDelegate,UITableViewDataSource,UISearchBarDelegate>
-(void)AcceptFriends:(CKFriendRequest *)req;
-(void)DenyFriends:(CKFriendRequest *)req;
- (void)prepareForNavigationBar:(UINavigationItem *)navi;
@end