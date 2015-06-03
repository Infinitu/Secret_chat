//
// Created by 김창규 on 15. 5. 31..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import "FriendsListController.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "FriendsTableCell.h"
#import "Friend.h"
#import "DetailViewController.h"
#import "Version.h"


@interface FriendsListController ()
@property NSArray *randomFiends;
@property NSArray *realFriends;
@property(nonatomic) CGPoint contentOffset;
@property(nonatomic) CGRect viewBounds;
@property(nonatomic) UIEdgeInsets scrollPoint;
@property UIActivityIndicatorView* activityIndicator;
@property UIBarButtonItem * indicatorBarButton;
@end

@implementation FriendsListController

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
    [self.tableView registerClass:FriendsTableCell.class forCellReuseIdentifier:@"friend_cell"];

    self.editButtonItem.tintColor = [UIColor
            colorWithRed: (CGFloat)(0xEF)/0xFF
                   green: (CGFloat)(0x8F)/0xFF
                    blue: (CGFloat)(0x14)/0xFF alpha:1];

    self.parentViewController.navigationItem.leftBarButtonItem = self.editButtonItem;

    self.activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    self.indicatorBarButton = [[UIBarButtonItem alloc] initWithCustomView:self.activityIndicator];

//    self.viewBounds = self.tableView.bounds;
//    self.tableView.clipsToBounds = NO;
//    self.contentOffset = self.tableView.contentOffset;
//    self.scrollPoint = self.tableView.scrollIndicatorInsets;
}
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:YES];
//    self.tableView.bounds = self.viewBounds;
//    self.tableView.contentOffset = self.contentOffset;
//    self.tableView.scrollIndicatorInsets = self.scrollPoint;
    if(self.tableView.contentOffset.y != self.contentOffset.y) {
        [self.tableView setContentOffset:self.contentOffset];
        self.contentOffset = CGPointZero;
    }
}


- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showDetail"]) {
        NSIndexPath *indexPath = [self.tableView indexPathForSelectedRow];
        [[segue destinationViewController] setDetailItem:[self getFriendAt:indexPath]];
//        self.viewBounds = self.tableView.bounds;
        self.contentOffset = self.tableView.contentOffset;
//        self.scrollPoint = self.tableView.scrollIndicatorInsets;
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
    RLMResults *result = [Friend allObjects];
    NSMutableArray *random =[NSMutableArray array];
    NSMutableArray *friend =[NSMutableArray array];

    for(Friend *fr in result) {
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
    FriendsTableCell *cell = [tableView dequeueReusableCellWithIdentifier:@"friend_cell" forIndexPath:indexPath];
    if(!cell) {
        cell = [[FriendsTableCell alloc] init];
    }
    Friend *fr = [self getFriendAt:indexPath];

    NSString *urlstr = fr.profileImg;
    if(![urlstr hasPrefix:@"http"])
        urlstr = [NSString stringWithFormat:@"%@://%@:%d/%@", DEFAULT_API_SCHEME, DEFAULT_API_HOST, DEFAULT_API_PORT, urlstr];

    [cell.profileImageView sd_setImageWithURL:[[NSURL alloc] initWithString:urlstr] placeholderImage:[UIImage imageNamed:@"ImageProfileDefault"] options:SDWebImageAllowInvalidSSLCertificates];
    cell.nicknameLabel.text = fr.nickname;

    return cell;
}

- (Friend *)getFriendAt:(NSIndexPath *)indexpath {
    return (indexpath.section ? self.realFriends : self.randomFiends)[(NSUInteger) indexpath.row];
}



@end