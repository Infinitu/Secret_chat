//
//  DetailViewController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "DetailViewController.h"
#import "BodyInterpreter.h"
#import "CKJsonParser.h"
#import "ProtocolSocket.h"

@interface DetailViewController ()
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UITextField *dialInput;
@property NSMutableArray *objects;
@end

@implementation DetailViewController


#pragma mark - Managing the detail item

- (void)setDetailItem:(id)newDetailItem {
    if (_detailItem != newDetailItem) {
        _detailItem = newDetailItem;
        [self.objects removeAllObjects];
        // Update the view.
        [self configureView];
    }
}

- (void)configureView {
    // Update the user interface for the detail item.
    if (self.detailItem) {
        self.title = self.detailItem;
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureView];
    
    if(self.objects == nil)
        _objects = [NSMutableArray array];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(newmsg:) name:@"newmsg" object:NULL];
}


-(void)newmsg:(NSNotification*)noti{
    NSLog(@"%@",noti.userInfo);
    if(![[noti.userInfo objectForKey:(NSString*)KEY_MSG_TYPE] isEqualToString:(NSString*)VALUE_MSG_TYPE_NEW_MSG_ARRIVAL])
        return;
    NSString* address = [noti.userInfo objectForKey:(NSString*)KEY_ADDRESS];
    if(![address isEqualToString:self.detailItem])
        return;
    NSDictionary* json = [CKJsonParser parseJson:[noti.userInfo objectForKey:(NSString*)KEY_MESSAGE_JSON]];
    [self addDialog:@"you" withText:[json objectForKey:@"message"]];
    
    NSString* datetimeStr = [noti.userInfo objectForKey:(NSString*)KEY_SEND_DATETIME];
    NSString* idxStr = [noti.userInfo objectForKey:(NSString*)KEY_INDEX];
    
    NSString* body = [NSString stringWithFormat:@"%@|%@|%@|",address,datetimeStr,idxStr];
    sendMessage(0x2111,(__bridge CFStringRef)body);

}


- (IBAction)sendDialog:(id)sender {
    NSString* text = self.dialInput.text;
    self.dialInput.text = nil;
    
    [self addDialog:@"me" withText:text];
    
    NSString* msg = [CKJsonParser serializeObject:@{@"type":@"text",@"message":text}];
    NSString* body = [NSString stringWithFormat:@"%@|%@|",self.detailItem,msg];
    sendMessage(0x2001,(__bridge CFStringRef)body);
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)addDialog:(NSString*)sender withText:(NSString*)text{
    [self.objects addObject:@{@"sender":sender,@"text":text}];
    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:self.objects.count-1 inSection:0];
    [self.tableView insertRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}


//TableView Deligate
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.objects.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"log" forIndexPath:indexPath];
    
    NSDictionary *object = self.objects[indexPath.row];
    ((UILabel*)[cell viewWithTag:2]).text = [object objectForKey:@"sender"];
    ((UILabel*)[cell viewWithTag:1]).text = [object objectForKey:@"text"];
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [self.objects removeObjectAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
    }
}

@end
