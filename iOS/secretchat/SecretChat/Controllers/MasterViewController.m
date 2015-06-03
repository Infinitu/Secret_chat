//
//  MasterViewController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "MasterViewController.h"
#import "AppDelegate.h"
#import "FriendsListController.h"
#import "FriendsSearchController.h"
#import "SettingController.h"


@interface MasterViewController ()
@property UITabBarItem *chatItem;
@property UITabBarItem *findItem;
@property UITabBarItem *settItem;

@property FriendsListController     *chatVC;
@property FriendsSearchController   *findVC;
@property SettingController         *settVC;



@end

@implementation MasterViewController

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.userData = ((AppDelegate *)[[UIApplication sharedApplication] delegate]).userData;

    [self.tabBar setTintColor:[UIColor
            colorWithRed: (CGFloat)(0xEF)/0xFF
                   green: (CGFloat)(0x8F)/0xFF
                    blue: (CGFloat)(0x14)/0xFF alpha:1]];


    _titleView = self.navigationItem.titleView;

    _chatItem = (UITabBarItem *)self.tabBar.items[0];
    _findItem = (UITabBarItem *)self.tabBar.items[1];
    _settItem = (UITabBarItem *)self.tabBar.items[2];

    _chatItem.selectedImage = [[UIImage imageNamed:@"IconTabChat"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    _findItem.selectedImage = [[UIImage imageNamed:@"IconTabFind"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    _settItem.selectedImage = [[UIImage imageNamed:@"IconTabSettings"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];

    _chatVC = self.viewControllers[0];
    _findVC = self.viewControllers[1];
    _settVC = self.viewControllers[2];

    [_chatVC prepareForNavigationBar:self.navigationItem];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshBadge) name:@"FriendsRequestUpdated" object:nil];

    [self refreshBadge];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)tabBar:(UITabBar *)tabBar didSelectItem:(UITabBarItem *)item {

    self.navigationItem.titleView = self.titleView;
    self.navigationItem.title=nil;
    self.navigationItem.leftBarButtonItem=nil;
    self.navigationItem.rightBarButtonItem=nil;
    if(item == _chatItem){
        [_chatVC prepareForNavigationBar:self.navigationItem];
    }
    else if(item == _findItem){
        [_findVC prepareForNavigationBar:self.navigationItem];
    }
    else if(item == _settItem){
        [_settVC prepareForNavigationBar:self.navigationItem];
    }


}

-(void)refreshBadge{
    RLMResults *result = [FriendRequest allObjects];
    int cnt = result.count;
    NSString *badge;
    if(cnt>0)
        badge= (cnt>99)?@"99+":[NSString stringWithFormat:@"%d",cnt];
    else
        badge = nil;
    self.tabBarItem.badgeValue=badge;
    _findItem.badgeValue = badge;
}




@end
