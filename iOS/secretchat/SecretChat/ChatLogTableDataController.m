//
//  ChatLogTableDataController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 8..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "ChatLogTableDataController.h"
@implementation ChatLogTableDataController

-(ChatLogTableDataController*)initWithRealm:(RLMRealm*)realm{
    self = [super init];
    if(self != nil){
        _objects = [NSMutableArray array];
        _pendingObjects = [NSMutableArray array];
        
    }
    return self;
}

-(BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath{
    return NO;
}

-(BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath{
    return NO;
}

-(NSIndexPath*)last{
    if(self.objects.count+self.pendingObjects.count<=0) return nil;
    return [NSIndexPath indexPathForItem:self.objects.count + self.pendingObjects.count-1 inSection:0];
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.objects.count + self.pendingObjects.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    Message *object;
    if(indexPath.row<self.objects.count){
        object=[self.objects objectAtIndex:indexPath.row];
    }
    else{
        object = [self.pendingObjects objectAtIndex:indexPath.row - self.objects.count];
    }
    
    ChatLogCell *cell;
    
    cell = [tableView dequeueReusableCellWithIdentifier:object.mine?@"mylog":@"yourlog" forIndexPath:indexPath];
    [cell prepareView:object];
    return cell;
}

-(CGFloat)tableView:tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    Message *object;
    if(indexPath.row<self.objects.count){
        object=[self.objects objectAtIndex:indexPath.row];
    }
    else{
        object = [self.pendingObjects objectAtIndex:indexPath.row - self.objects.count];
    }
    return [ChatLogCell guessTextSize:object.text withWidth:((UITableView*)tableView).frame.size.width].height+20;
}

-(CGFloat)tableView:tableView estimatedHeightForRowAtIndexPath:(NSIndexPath *)indexPath{
    Message *object;
    if(indexPath.row<self.objects.count){
        object=[self.objects objectAtIndex:indexPath.row];
    }
    else{
        object = [self.pendingObjects objectAtIndex:indexPath.row - self.objects.count];
    }
    return [ChatLogCell guessTextSize:object.text withWidth:((UITableView*)tableView).frame.size.width].height+20;
}

@end
