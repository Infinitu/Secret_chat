//
//  ChatLogTableDataController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 8..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKChatLogTableDataController.h"
@implementation CKChatLogTableDataController

-(CKChatLogTableDataController*)initWithRealm:(RLMRealm*)realm{
    self = [super init];
    if(self != nil){
        _objects = [NSMutableArray array];
        _pendingObjects = [NSMutableArray array];
        _isScrollFollwing = true;
        _realm = realm;
    }
    return self;
}

-(void)reloadAllMessages{
    [_objects removeAllObjects];
    [_pendingObjects removeAllObjects];
    
    RLMResults *result = [[CKMessage allObjectsInRealm:self.realm] sortedResultsUsingProperty:@"datetime" ascending:YES];
    for(CKMessage *msg in result){
        if(msg.datetime<0)
            [_pendingObjects addObject:msg];
        else
            [_objects addObject:msg];
    }
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    return NO;
}

-(BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath{
    return NO;
}

-(NSIndexPath*)last{
    if(self.objects.count<=0) return nil;
    return [NSIndexPath indexPathForItem:self.objects.count-1 inSection:0];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.objects.count ;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    CKMessage *object;
    BOOL conti;
    if(indexPath.row<self.objects.count){
        object= self.objects[(NSUInteger) indexPath.row];
        if(indexPath.row +1 == self.objects.count)
            conti = NO;
        else
            conti = ((CKMessage *)self.objects[(NSUInteger) indexPath.row+1]).mine == object.mine;
    }
    else{
        object = self.pendingObjects[indexPath.row - self.objects.count];
        conti = YES;
    }
    
    CKChatLogCell *cell;
    
    cell = [tableView dequeueReusableCellWithIdentifier:object.mine?conti?@"mylog_continue":@"mylog":conti?@"yourlog_continue":@"yourlog" forIndexPath:indexPath];
    [cell prepareView:object];
    return cell;
}

-(CGFloat)tableView:tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return [self tableView:tableView estimatedHeightForRowAtIndexPath:indexPath];
}

-(CGFloat)tableView:tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath{
    CKMessage *object;
    BOOL conti = NO;
    if(indexPath.row<self.objects.count){
        object= self.objects[(NSUInteger) indexPath.row];
        if(indexPath.row +1 == self.objects.count)
            conti = NO;
        else
            conti = ((CKMessage *)self.objects[(NSUInteger) indexPath.row+1]).mine == object.mine;

    }
    else{
        object = self.pendingObjects[indexPath.row - self.objects.count];
        conti = YES;
    }
    return [CKChatLogCell guessTextSize:object.text withWidth:((UITableView *) tableView).frame.size.width].height+7+7+(conti?2:8);
}


#pragma mark manage scrolls

-(void)scrollViewDidScroll:(UIScrollView *)scrollView{
    _isScrollFollwing = scrollView.contentSize.height-scrollView.frame.size.height<=scrollView.contentOffset.y;
}

-(void)updateScroll:(UIScrollView*)scroll{
    if(self.isScrollFollwing){
        CGFloat y = scroll.contentSize.height-scroll.frame.size.height+64;
        NSLog(@"y = %f",y);
        if(y<0)return;
        [scroll setContentOffset:CGPointMake(0,y)];
    }
}

@end
