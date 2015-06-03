//
//  SettingControllerTableViewController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 6. 1..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <SDWebImage/UIImageView+WebCache.h>
#import "SettingController.h"
#import "AppDelegate.h"
#import "UserData.h"
#import "Version.h"

@interface SettingController ()
@property (weak, nonatomic) IBOutlet UISwitch *nickname_activation;
@property (weak, nonatomic) IBOutlet UILabel *nickname_tag_label;
@property (weak, nonatomic) IBOutlet UILabel *nickname_label;
@property (weak, nonatomic) IBOutlet UIImageView *profile_image_view;
@property (weak, nonatomic) IBOutlet UITableViewCell *nickname_tag_cell;
@property UserData *userdata;
@end

@implementation SettingController

- (void)prepareForNavigationBar:(UINavigationItem *)navi {
    navi.title = @"설정";
}

- (void)viewDidLoad {
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;

    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;

    UserData *ud = ((AppDelegate *)[[UIApplication sharedApplication] delegate]).userData;
    self.userdata = ud;

    CGSize parsize = self.profile_image_view.superview.frame.size;
    parsize.width = [UIScreen mainScreen].bounds.size.width;
    CGFloat b=parsize.height/2;
    CGRect imgRect = CGRectMake(parsize.width/2-b/2, parsize.height/2-b/2, b,b);
    [self.profile_image_view setFrame:imgRect];
    self.profile_image_view.layer.cornerRadius = b/2;
    self.profile_image_view.layer.masksToBounds = YES;
    NSString *urlstr = ud.profile.profileImg;
    if(![urlstr hasPrefix:@"http"])
        urlstr = [NSString stringWithFormat:@"%@://%@:%d/%@", DEFAULT_API_SCHEME, DEFAULT_API_HOST, DEFAULT_API_PORT, urlstr];
    [self.profile_image_view sd_setImageWithURL:[[NSURL alloc] initWithString:urlstr] placeholderImage:[UIImage imageNamed:@"ImageProfileDefault"] options:SDWebImageAllowInvalidSSLCertificates];
    self.nickname_label.text = ud.profile.nickname;
    self.nickname_tag_cell.hidden = true;

    CGRect swRect = self.nickname_activation.frame;
    swRect.origin.x = parsize.width - swRect.size.width-10;
    [self.nickname_activation setFrame:swRect];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//Nickname Tag

- (IBAction)switchChanged:(id)sender {
    if(self.nickname_activation.on){
        self.nickname_activation.enabled = false;
        NSString *accessToken = self.userdata.accessToken;
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
                                   self.nickname_tag_label.text = [NSString stringWithUTF8String:mdata.bytes];;
                                   self.nickname_activation.enabled = true;
                                   self.nickname_tag_cell.hidden=false;
                               }];
        return;
    }
    self.nickname_tag_cell.hidden=true;
    //disable NicknameTag;
}


-(void)failToGetTag{
    self.nickname_activation.on = false;
    self.nickname_activation.enabled = true;
    UIAlertView *theAlert = [[UIAlertView alloc] initWithTitle:@"Oops"
                                                       message:@"Getting Tag had been failed. Please try agin."
                                                      delegate:self
                                             cancelButtonTitle:@"Ok"
                                             otherButtonTitles:nil];
    [theAlert show];
}

- (BOOL)tableView:(UITableView *)tableView canPerformAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender {
    return indexPath.section != 1;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
}


@end
