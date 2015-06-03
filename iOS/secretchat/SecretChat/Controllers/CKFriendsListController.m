//
//  CKFriendsListController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 6. 3..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKFriendsListController.h"
#import "CKFriendsTableCell.h"
#import "CKFriend.h"
#import "Version.h"
#import "CKChatRoomController.h"
#import <SDWebImage/UIImageView+WebCache.h>

@interface CKFriendsListController ()

@property NSArray *randomFiends;
@property NSArray *realFriends;
@property(nonatomic) CGPoint contentOffset;
@property UIActivityIndicatorView* activityIndicator;
@property UIBarButtonItem * indicatorBarButton;

@property UIEdgeInsets scrollInset;

@end
@implementation CKFriendsListController

- (void)prepareForNavigationBar:(UINavigationItem *)navi {
    navi.title = @"채팅";
    navi.leftBarButtonItem = self.editButtonItem;
    navi.rightBarButtonItem = self.indicatorBarButton;
    [self.activityIndicator startAnimating];
}


- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter]
            addObserver:self
               selector:@selector(resetFriendsList)
                   name:@"FriendListChanged"
                 object:nil];
    [self resetFriendsList];


    //set for lat
    [self.tableView registerClass:CKFriendsTableCell.class forCellReuseIdentifier:@"friend_cell"];

    self.editButtonItem.tintColor = [UIColor
            colorWithRed: (CGFloat)(0xEF)/0xFF
                   green: (CGFloat)(0x8F)/0xFF
                    blue: (CGFloat)(0x14)/0xFF alpha:1];

    self.parentViewController.navigationItem.leftBarButtonItem = self.editButtonItem;

    self.activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    self.indicatorBarButton = [[UIBarButtonItem alloc] initWithCustomView:self.activityIndicator];

    self.scrollInset = self.tableView.scrollIndicatorInsets;
}



- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:YES];
    self.tableView.scrollIndicatorInsets = self.scrollInset;
}


- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showDetail"]) {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        [[segue destinationViewController] setDetailItem:[self getFriendAt:indexPath]];
        self.scrollInset = self.tableView.scrollIndicatorInsets;
        [self.tableView deselectRowAtIndexPath:indexPath animated:YES];
    }
}


- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {

    [self performSegueWithIdentifier:@"showDetail" sender:tableView];
}


//////////////////////////////////////////////////////////////
//                      datasource                          //
//////////////////////////////////////////////////////////////
-(void)resetFriendsList{
    RLMResults *result = [CKFriend allObjects];
    NSMutableArray *random =[NSMutableArray array];
    NSMutableArray *friend =[NSMutableArray array];

    for(CKFriend *fr in result) {
        [([fr isRandom] ? random : friend) addObject:fr];
    }
    _randomFiends = random;
    _realFriends = friend;
    [self.tableView reloadData];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(section==0)
        return self.randomFiends.count;
    return self.realFriends.count;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if(section)
        return @"친구";
    return @"랜덤채팅";
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    CKFriendsTableCell *cell = [tableView dequeueReusableCellWithIdentifier:@"friend_cell" forIndexPath:indexPath];
    if(!cell) {
        cell = [[CKFriendsTableCell alloc] init];
    }
    CKFriend *fr = [self getFriendAt:indexPath];

    NSString *urlstr = fr.profileImg;
    if(![urlstr hasPrefix:@"http"])
        urlstr = [NSString stringWithFormat:@"%@://%@:%d/%@", DEFAULT_API_SCHEME, DEFAULT_API_HOST, DEFAULT_API_PORT, urlstr];

    [cell.profileImageView sd_setImageWithURL:[[NSURL alloc] initWithString:urlstr] placeholderImage:[UIImage imageNamed:@"ImageProfileDefault"] options:SDWebImageAllowInvalidSSLCertificates];
    cell.nicknameLabel.text = fr.nickname;

    return cell;
}

- (CKFriend *)getFriendAt:(NSIndexPath *)indexpath {
    return (indexpath.section ? self.realFriends : self.randomFiends)[(NSUInteger) indexpath.row];
}



@end
