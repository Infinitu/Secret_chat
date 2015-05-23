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

@property NSString *nicknameTag;

@end
@implementation FriendsSearchController

CGSize windowSize;

-(void)viewDidLoad{
    [super viewDidLoad];
    [self refreshObjects];
    windowSize = [UIScreen mainScreen].bounds.size;
    _nicknameTagActivator = [self.tableView dequeueReusableCellWithIdentifier:@"activateNicknameTag"];
    _nicknameTagActivator_TitleLabel = (UILabel *) [_nicknameTagActivator viewWithTag:2];
    _nicknameTagActivator_SubtitleLabel = (UILabel *) [_nicknameTagActivator viewWithTag:1];
    _nicknameTagActivator_Switch = (UISwitch *) [_nicknameTagActivator viewWithTag:3];

    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    self.tableView.allowsSelection = NO;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshObjects) name:@"FriendsRequestUpdated" object:nil];
    [self hideResultWindow];
}

-(void)refreshObjects{
    _arrivedRequest = [NSMutableArray array];
    for(FriendRequest *req in [FriendRequest allObjects])
        [self.arrivedRequest addObject:req];
    [self.tableView reloadData];
}

//Layout
-(IBAction)CloseResultWindow:(id)sender{
    [self hideResultWindow];
}

-(void)showResultWindowWithResult:(NSDictionary*)dictionary{
    self.friendsSearchNoResult.hidden = true;
    self.friendsSearchResult.hidden = true;
    UIView *resWindow = self.friendsSearchResult;
    if(!dictionary) resWindow = self.friendsSearchNoResult;
    else{
        resWindow = self.friendsSearchResult;
        UIImageView *profile = (UIImageView *) [self.friendsSearchResult viewWithTag:1];
        UILabel *nickname = (UILabel *) [self.friendsSearchResult viewWithTag:2];

        [profile sd_setImageWithURL:dictionary[@"imageUrl"]];
        nickname.text = dictionary[@"nickName"];
    }
    resWindow.hidden=false;
    CGFloat posY = self.searchBar.frame.origin.y+self.searchBar.frame.size.height;
    [resWindow setFrame:CGRectMake(0,posY,windowSize.width,resWindow.frame.size.height)];
    posY+=resWindow.frame.size.height+10;
    CGRect frame = self.tableView.frame;
    frame.origin.y = posY;
    frame.size.height = windowSize.height-posY;
    [UIView animateWithDuration:0.5 animations:^{[self.tableView setFrame:frame];}];
}

-(void)hideResultWindow{
    self.friendsSearchNoResult.hidden = true;
    self.friendsSearchResult.hidden = true;
    CGFloat posY = self.searchBar.frame.origin.y+self.searchBar.frame.size.height;
    CGRect frame = self.tableView.frame;
    frame.origin.y = posY;
    frame.size.height = windowSize.height-posY;
    [UIView animateWithDuration:0.5 animations:^{[self.tableView setFrame:frame];}];
}
    
//Nickname Tag
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

- (IBAction)switchChanged:(id)sender {
    if(self.nicknameTagActivator_Switch.on){
        self.nicknameTagActivator_SubtitleLabel.text = @"pending...";
        self.nicknameTagActivator_Switch.enabled = false;
        NSString *accessToken = ((MasterViewController *)self.tabBarController).userData.accessToken;
        NSURL *joinURL =
                [[NSURL alloc] initWithScheme:DEFAULT_API_SCHEME
                                         host:[NSString stringWithFormat:@"%@:%d",DEFAULT_API_HOST,DEFAULT_API_PORT]
                                         path:@"/getTag"];

        NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:joinURL];

        req.HTTPMethod = @"POST";
        [req addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [req setHTTPBody:[[NSString stringWithFormat:@"{\"accessToken\":\"%@\"}",accessToken] dataUsingEncoding:NSUTF8StringEncoding]];
        [NSURLRequest setAllowsAnyHTTPSCertificate:YES forHost:DEFAULT_API_HOST];
        [NSURLConnection sendAsynchronousRequest:req
                                           queue:[NSOperationQueue mainQueue]
                               completionHandler:^(NSURLResponse *res, NSData *data, NSError *err){
                                   NSHTTPURLResponse *response = (NSHTTPURLResponse *) res;
                                   if(response.statusCode != 200) return [self failToGetTag];
                                   NSMutableData *mdata = [NSMutableData dataWithData:data];
                                   uint8_t nullChar = '\0';
                                   [mdata appendBytes: &nullChar length:1];
                                   self.nicknameTag = [NSString stringWithUTF8String:mdata.bytes];;
                                   self.nicknameTagActivator_Switch.enabled = true;

                                   self.nicknameTagActivator_TitleLabel.text = @"Inactivate Nickname Tag";
                                   self.nicknameTagActivator_SubtitleLabel.text = @"it become inactive after 1 hour automatically";
                                   [self.tableView insertRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:1 inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
                               }];
        return;
    }
    self.nicknameTagActivator_TitleLabel.text = @"Activate Nickname Tag";
    self.nicknameTagActivator_SubtitleLabel.text = @"it become inactive after 1 hour automatically";
    [self.tableView deleteRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:1 inSection:0]] withRowAnimation:UITableViewRowAnimationAutomatic];
    //disable NicknameTag;
}

-(void)failToGetTag{
    self.nicknameTagActivator_Switch.on = false;
    self.nicknameTagActivator_Switch.enabled = true;
    UIAlertView *theAlert = [[UIAlertView alloc] initWithTitle:@"Oops"
                                                       message:@"Getting Tag had been failed. Please try agin."
                                                      delegate:self
                                             cancelButtonTitle:@"Ok"
                                             otherButtonTitles:nil];
    [theAlert show];
}

//Friends
-(void)AcceptFriends:(FriendRequest *)req{
    [[FriendManager getInstance] sendFriendResponseTo:req.address withAction:@"accept"];
}
- (IBAction)addFriends:(id)sender {
    [[FriendManager getInstance] sendFriendRequestTo:self.userFound[@"_id"] withMessage:nil]; //todo!!
    [self hideResultWindow];
}

//TableView
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if(section){
        return self.arrivedRequest.count;
    }
    return (self.nicknameTagActivator_Switch.on && self.nicknameTagActivator_Switch.isEnabled) ? 2 : 1;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return self.arrivedRequest.count?2:1;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    if(!section) return nil;
    return @"Friend Requests";
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if(!indexPath.section){
        if(!indexPath.row) return self.nicknameTagActivator;
        UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"nicknameTag"];
        ((UILabel *)[cell viewWithTag:1]).text = self.nicknameTag;
        return cell;
    }
    FriendRequest *obj = self.arrivedRequest[(NSUInteger) indexPath.row];
    FriendRequestTableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"friendRequest"];
    cell.caller=self;
    cell.nickname.text = obj.nickname;
    cell.req = obj;
    [cell.profileImageView sd_setImageWithURL:[NSURL URLWithString:obj.profileImage]];

    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return indexPath.section != 0;
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

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    if(indexPath.section) return 66;
    return tableView.rowHeight;
}


@end