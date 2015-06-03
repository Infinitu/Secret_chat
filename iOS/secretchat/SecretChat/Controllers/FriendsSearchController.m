//
// Created by 김창규 on 15. 5. 20..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import "FriendsSearchController.h"
#import "Version.h"
#import "MasterViewController.h"
#import "FriendRequestTableViewCell.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import "UserData.h"
#import "CKJsonParser.h"
#import "FriendManager.h"

@interface FriendsSearchController ()
@property (weak, nonatomic) IBOutlet UIView *friendsSearchResult;
@property (weak, nonatomic) IBOutlet UIView *friendsSearchNoResult;
@property (weak, nonatomic) IBOutlet UISearchBar *searchBar;
    @property (weak, nonatomic) IBOutlet UITableView *tableView;
@property UITableViewCell *nicknameTagActivator;
@property UILabel *nicknameTagActivator_TitleLabel;
@property UILabel *nicknameTagActivator_SubtitleLabel;
@property UISwitch *nicknameTagActivator_Switch;
@property NSDictionary *userFound;
@property NSMutableArray *arrivedRequest;

@property UIView *originTitle;
@property CGRect originSearchFrame;
@property CGRect originSearchBound;

@property NSString *nicknameTag;

@end
@implementation FriendsSearchController

CGSize windowSize;

-(void)viewDidLoad{
    [super viewDidLoad];
    [self refreshObjects];
    windowSize = [UIScreen mainScreen].bounds.size;
    
    UIImageView *view = (UIImageView *) [self.friendsSearchResult viewWithTag:1];
    view.layer.cornerRadius = view.frame.size.width/2;
    view.layer.masksToBounds = YES;

    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    self.tableView.allowsSelection = NO;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshObjects) name:@"FriendsRequestUpdated" object:nil];
    self.originSearchFrame = self.searchBar.frame;
    [self hideResultWindow];
}

-(void)viewDidAppear:(BOOL)animated {
    [self.view addSubview:self.searchBar];
    [self hideResultWindow];
}
- (void)prepareForNavigationBar:(UINavigationItem *)navi {
    navi.title = @"친구찾기";
}

-(void)refreshObjects{
    _arrivedRequest = [NSMutableArray array];
    RLMResults *result = [FriendRequest allObjects];
    NSInteger cnt = [result count];
    for(FriendRequest *req in result)
        [self.arrivedRequest addObject:req];


    [self.tableView reloadData];

}

//Layout
-(void)showResultWindowWithResult:(NSDictionary*)dictionary{
    self.friendsSearchNoResult.hidden = true;
    self.friendsSearchResult.hidden = true;
    UIView *resWindow = self.friendsSearchResult;
    if(!dictionary) resWindow = self.friendsSearchNoResult;
    else{
        resWindow = self.friendsSearchResult;
        UIImageView *profile = (UIImageView *) [self.friendsSearchResult viewWithTag:1];
        UILabel *nickname = (UILabel *) [self.friendsSearchResult viewWithTag:2];
        NSString *urlstr = dictionary[@"imageUrl"];
        if(![urlstr hasPrefix:@"http"])
            urlstr = [NSString stringWithFormat:@"%@://%@:%d/%@", DEFAULT_API_SCHEME, DEFAULT_API_HOST, DEFAULT_API_PORT, urlstr];
        [profile sd_setImageWithURL:[[NSURL alloc] initWithString:urlstr] placeholderImage:[UIImage imageNamed:@"ImageProfileDefault"] options:SDWebImageAllowInvalidSSLCertificates];
        nickname.text = dictionary[@"nickName"];
    }
    resWindow.hidden=false;
    [resWindow setFrame:CGRectMake(0,64,windowSize.width,resWindow.frame.size.height)];
    self.tableView.hidden=YES;
}

-(void)hideResultWindow{
    self.friendsSearchNoResult.hidden = true;
    self.friendsSearchResult.hidden = true;
    self.tableView.hidden = false;
    [self.searchBar setFrame:self.originSearchFrame];
}

//Friends
-(void)AcceptFriends:(FriendRequest *)req{
    [[FriendManager getInstance] sendFriendResponseTo:req.address withAction:@"accept"];
//    [[RLMRealm defaultRealm] beginWriteTransaction];
//    [FriendRequest delete:req];
//    [[RLMRealm defaultRealm] commitWriteTransaction];

    [[NSNotificationCenter defaultCenter] postNotificationName:@"FriendsRequestUpdated" object:self];
    //[self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:[self.arrivedRequest indexOfObject:req] inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
    [_arrivedRequest removeObject:req];
    [self.tableView reloadData];
}

-(void)DenyFriends:(FriendRequest *)req{
    [[FriendManager getInstance] sendFriendResponseTo:req.address withAction:@"deny"];
//    [[RLMRealm defaultRealm] beginWriteTransaction];
//    [FriendRequest delete:req];
//    [[RLMRealm defaultRealm] commitWriteTransaction];
//    [self refreshObjects];
//    [self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:[self.arrivedRequest indexOfObject:req] inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
    [_arrivedRequest removeObject:req];
    [self.tableView reloadData];

}
- (IBAction)addFriends:(id)sender {
    [[FriendManager getInstance] sendFriendRequestTo:self.userFound[@"_id"] withMessage:nil]; //todo!!
    [self hideResultWindow];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    [self.searchBar resignFirstResponder];
    [searchBar setUserInteractionEnabled:NO];
    NSString *query = searchBar.text;
    NSURL *joinURL =
            [[NSURL alloc] initWithScheme:DEFAULT_API_SCHEME
                                     host:[NSString stringWithFormat:@"%@:%d",DEFAULT_API_HOST,DEFAULT_API_PORT]
                                     path:@"/addfriend"];

    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:joinURL];

    req.HTTPMethod = @"POST";
    [req addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [req setHTTPBody:[[NSString stringWithFormat:@"{\"nickNameTag\":\"%@\"}",query] dataUsingEncoding:NSUTF8StringEncoding]];
    [NSURLRequest setAllowsAnyHTTPSCertificate:YES forHost:DEFAULT_API_HOST];
    [NSURLConnection sendAsynchronousRequest:req
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *res, NSData *data, NSError *err){
                               [searchBar setUserInteractionEnabled:YES];
                               NSHTTPURLResponse *response = (NSHTTPURLResponse *) res;
                               if(response.statusCode != 200) return [self showResultWindowWithResult:nil];
                               NSMutableData *mdata = [NSMutableData dataWithData:data];
                               uint8_t nullChar = '\0';
                               [mdata appendBytes: &nullChar length:1];
                               NSString *resultStr = [NSString stringWithUTF8String:mdata.bytes];
                               self.userFound = [CKJsonParser parseJson:resultStr];
                               NSLog(@"friendsFound\n%@",self.userFound);
                               [self showResultWindowWithResult:self.userFound];
                           }];
}

//TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
        return self.arrivedRequest.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    FriendRequest *obj = self.arrivedRequest[(NSUInteger) indexPath.row];
    FriendRequestTableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"friendRequest"];
    cell.caller=self;
    cell.nickname.text = obj.nickname;
    cell.req = obj;
    NSString *urlstr = obj.profileImage;
    if(![urlstr hasPrefix:@"http"])
        urlstr = [NSString stringWithFormat:@"%@://%@:%d/%@", DEFAULT_API_SCHEME, DEFAULT_API_HOST, DEFAULT_API_PORT, urlstr];
    [cell.profileImageView sd_setImageWithURL:[NSURL URLWithString:urlstr] placeholderImage:[UIImage imageNamed:@"ImageProfileDefault"] completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, NSURL *imageURL){
        NSLog(@"break");
    }];

    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [[RLMRealm defaultRealm] beginWriteTransaction];
        [FriendRequest delete:self.arrivedRequest[(NSUInteger)indexPath.row]];
        [[RLMRealm defaultRealm] commitWriteTransaction];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
        [self refreshObjects];
    }
}

- (BOOL)searchBarShouldBeginEditing:(UISearchBar *)searchBar {
    self.originSearchFrame = searchBar.frame;
    self.originTitle = self.parentViewController.navigationItem.titleView;
    self.parentViewController.navigationItem.titleView = searchBar;
    searchBar.showsCancelButton = YES;
    self.tableView.hidden = true;
    return YES;
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {
    self.parentViewController.navigationItem.titleView = self.originTitle;
    self.parentViewController.navigationItem.title =@"친구찾기";
    [self.view addSubview:searchBar];
    [searchBar setFrame:self.originSearchFrame];
    searchBar.showsCancelButton = NO;
    searchBar.text = @"";
    [self hideResultWindow];
}


@end