//
//  FriendsCollectionViewController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 20..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "FriendsCollectionViewController.h"
#import "DetailViewController.h"
#import "NetworkManager.h"
#import "Version.h"
#import "AppDelegate.h"
#import "FriendsCollectionViewCell.h"
#import <SDWebImage/UIImageView+WebCache.h>

@interface FriendsCollectionViewController ()

@property NSMutableArray *objects;

@end

@implementation FriendsCollectionViewController

-(void)viewDidLoad{
    [self refreshFriendsList];
    [self.collectionView setContentInset:UIEdgeInsetsMake(20, 20, 20, 20)];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFriendsList) name:@"FriendListChanged" object:nil];
}

-(void)refreshFriendsList{
    RLMResults *result = [Friend allObjects];
    self.objects =[NSMutableArray arrayWithCapacity:result.count];
    NSLog(@"%@",[RLMRealm defaultRealmPath]);
    for(Friend *fr in result)
        [self.objects addObject:fr];
}

#pragma mark - Segues

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([[segue identifier] isEqualToString:@"showDetail"]) {
        NSIndexPath *indexPath = [self.collectionView indexPathsForSelectedItems][0];
        NSDate *object = self.objects[(NSUInteger) (indexPath.row-1)];
        [[segue destinationViewController] setDetailItem:object];
    }
}

#pragma mark - Table View

//- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
//    return 1;
//}
//
//- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
//    return self.objects.count;
//}
//
//- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
//    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell" forIndexPath:indexPath];
//
//    NSDate *object = self.objects[indexPath.row];
//    cell.textLabel.text = ((Friend*)object).nickname;
//    return cell;
//}
//
//- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
//    // Return NO if you do not want the specified item to be editable.
//    return YES;
//}
//
//- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
//    if (editingStyle == UITableViewCellEditingStyleDelete) {
//        [self.objects removeObjectAtIndex:indexPath.row];
//        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
//    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
//        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
//    }
//}
//



/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

#pragma mark <UICollectionViewDataSource>

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.objects.count+1;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    if(indexPath.row ==0)
        return [collectionView dequeueReusableCellWithReuseIdentifier:@"newRandom"forIndexPath:indexPath];
    FriendsCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"friend" forIndexPath:indexPath];
    cell.friend = self.objects[(NSUInteger) (indexPath.row-1)];
    cell.nicknameLabel.text = cell.friend.nickname;
    [cell.profileImageView sd_setImageWithURL:[NSURL URLWithString:cell.friend.profileImg]];
    
    return cell;
}

- (CGSize)collectionView:(UICollectionView *)collectionView
                  layout:(UICollectionViewLayout *)collectionViewLayout
  sizeForItemAtIndexPath:(NSIndexPath *)indexPath{
    return CGSizeMake(150, 158);
}

#pragma mark <UICollectionViewDelegate>

/*
 // Uncomment this method to specify if the specified item should be highlighted during tracking
 - (BOOL)collectionView:(UICollectionView *)collectionView shouldHighlightItemAtIndexPath:(NSIndexPath *)indexPath {
	return YES;
 }
 */

/*
 // Uncomment this method to specify if the specified item should be selected
 - (BOOL)collectionView:(UICollectionView *)collectionView shouldSelectItemAtIndexPath:(NSIndexPath *)indexPath {
 return YES;
 }
 */

/*
 // Uncomment these methods to specify if an action menu should be displayed for the specified item, and react to actions performed on the item
 - (BOOL)collectionView:(UICollectionView *)collectionView shouldShowMenuForItemAtIndexPath:(NSIndexPath *)indexPath {
	return NO;
 }
 
 - (BOOL)collectionView:(UICollectionView *)collectionView canPerformAction:(SEL)action forItemAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender {
	return NO;
 }
 
 - (void)collectionView:(UICollectionView *)collectionView performAction:(SEL)action forItemAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender {
	
 }
 */
@end
