//
//  MasterViewController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "MasterViewController.h"
#import "AppDelegate.h"
#import "UserData.h"
#import "FriendRequest.h"
#import <SDWebImage/UIImageView+WebCache.h>


@interface MasterViewController ()
@end

@implementation MasterViewController

- (void)awakeFromNib {
    [super awakeFromNib];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    self.userData = ((AppDelegate *)[[UIApplication sharedApplication] delegate]).userData;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}



@end
