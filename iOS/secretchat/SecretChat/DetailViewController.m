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
#import "ChatLogTableDataController.h"
#import "MessageDispatcher.h"
@interface DetailViewController ()
@property UITextView *ChatInputDump;
@property (weak, nonatomic) IBOutlet UIView *ChatContainer;
@property (weak, nonatomic) IBOutlet UITableView *ChatScroll;
@property (weak, nonatomic) IBOutlet UITextView *ChatInput;
@property (weak, nonatomic) IBOutlet UIButton *ChatSend;
@property ChatLogTableDataController* ChatLogs;
@property RLMRealm *realm;
@property Friend *friend;
@end

@implementation DetailViewController


#pragma mark - Managing the detail item

- (void)setDetailItem:(id)friend {
    self.friend = friend;
}

- (void)configureView{
//    if (self.friend) {
//        self.title = self.friend.nickname;
//        if(self.realm==nil || ![self.realm.path isEqualToString:[self.friend chatRealmPath]]){
//            self.realm = [RLMRealm realmWithPath:[self.friend chatRealmPath]
//                                   encryptionKey:[[NSData alloc] initWithBase64EncodedString:self.friend.encKey options:0]
//                                        readOnly:YES
//                                           error:NULL];
//            
//            [self.realm addNotificationBlock:^(NSString *notification, RLMRealm *realm) {
//                [self refreshChat];
//            }];
            self.ChatLogs = [[ChatLogTableDataController alloc] initWithRealm:self.realm];
    
    [self.ChatScroll setContentInset:UIEdgeInsetsMake(72, 0, 0, 0)];
    self.ChatScroll.dataSource = self.ChatLogs;
    self.ChatScroll.delegate = self.ChatLogs;
    self.ChatScroll.allowsSelection = false;
//        }
//    }
}

#pragma mark - lifecycle

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureView];
    [self setViewLayout:[UIScreen mainScreen].bounds.size];
    if(self.ChatInputDump == nil)
        self.ChatInputDump = [self.ChatInputDump copy];
    
    [self registerNotification];
    Message *msg = [[Message alloc]init];
    msg.text = @"hello";
    msg.datetime = [NSDate date].timeIntervalSince1970*1000;
    msg.mine = true;
    
    Message *msg2 = [[Message alloc]init];
    msg2.text = @"hello\n\n\n\bye";
    msg2.datetime = [NSDate date].timeIntervalSince1970*1000;
    msg2.mine = false;
    
    Message *msg3 = [[Message alloc]init];
    msg3.text = @"helloasdfasdfasldkfjalskdjfa;lksdjf;alskdjf;aslkdjfa;slkdjfa;slkdfja;slkdfjalskjdhflaskjdhflaskjdhflaskjdhflsakjdfhalskdjfh";
    msg3.datetime = [NSDate date].timeIntervalSince1970*1000;
    msg3.mine = true;
    [self.ChatLogs.objects addObject:msg];
    [self.ChatLogs.objects addObject:msg2];
    [self.ChatLogs.objects addObject:msg3];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void) registerNotification{
    NSNotificationCenter *notiCenter =[NSNotificationCenter defaultCenter];
    [notiCenter addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:NULL];
    [notiCenter addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:NULL];
}

#pragma mark - ui layout

-(void)keyboardWillShow:(NSNotification*)noti{
    CGRect rect = [((NSValue*)[noti.userInfo objectForKey:UIKeyboardFrameEndUserInfoKey]) CGRectValue];
    [self setViewLayout:[[UIScreen mainScreen] bounds].size withKeyboard:rect];
}

-(void)keyboardWillHide:(NSNotification*)noti{
    [self setViewLayout:[[UIScreen mainScreen] bounds].size];
}


-(void)setViewLayout:(CGSize)screen{
    [self setViewLayout:screen withKeyboard:CGRectZero];
}

-(void)setViewLayout:(CGSize)screen withKeyboard:(CGRect)keyboard{
    CGFloat height = screen.height-keyboard.size.height;
    
    
    CGFloat sendBtnWid = self.ChatSend.frame.size.width;
    CGRect  inputRect = self.ChatInput.frame;
    
    
    CGFloat newWidth = screen.width-sendBtnWid-30;
    self.ChatInputDump.text = self.ChatInput.text;
    CGSize newSize = [self.ChatInputDump sizeThatFits:CGSizeMake(newWidth, MAXFLOAT)];
    CGFloat newheight = newSize.height;
    
    newheight = newheight>70?70:newheight;
    newheight = newheight<30?30:newheight;
    NSLog(@"%f  \t%f\t%f",(int)newheight - self.ChatInput.frame.size.height,self.ChatInput.frame.size.height, newSize.height);

    
    CGRect newContainerFrame = CGRectMake(0, height-newheight-10, screen.width, newheight+10);
    CGRect newInputFrame = CGRectMake(10, 5, screen.width-sendBtnWid-30, newheight);
    CGRect newSendBtnFrame = CGRectMake(screen.width-sendBtnWid-10, 5 , sendBtnWid, newheight);
    CGRect newScrollFrame = CGRectMake(0, 0, screen.width, height-newContainerFrame.size.height);
    
    
    [self.ChatContainer setFrame:newContainerFrame];
    [self.ChatScroll setFrame:newScrollFrame];//  fromView:self.ChatContainer];
    [self.ChatInput setFrame:newInputFrame];//   fromView:self.ChatContainer];
    [self.ChatSend  setFrame:newSendBtnFrame]; //fromView:self.ChatContainer];
}
long lastStr;
- (void)textViewDidChange:(UITextView *)textView{
    CGFloat height = self.ChatScroll.frame.size.height+self.ChatContainer.frame.size.height;
    CGFloat width = self.ChatScroll.frame.size.width;
    
    CGFloat sendBtnWid = self.ChatSend.frame.size.width;
    CGRect  inputRect = self.ChatInput.frame;
    
    
    CGFloat newWidth = width-sendBtnWid-30;
    
    self.ChatInputDump.text = self.ChatInput.text;
    CGSize newSize = [self.ChatInputDump sizeThatFits:CGSizeMake(newWidth, MAXFLOAT)];
    CGFloat newheight = newSize.height;
    
    newheight = newheight>70?70:newheight;
    newheight = newheight<30?30:newheight;
    NSLog(@"%f  \t%f\t%f",(int)newheight - self.ChatInput.frame.size.height,self.ChatInput.frame.size.height, newSize.height);
    
    
    CGRect newContainerFrame = CGRectMake(0, height-newheight-10, width, newheight+10);
    CGRect newInputFrame = CGRectMake(10, 5, width-sendBtnWid-30, newheight);
    CGRect newSendBtnFrame = CGRectMake(width-sendBtnWid-10, 5 , sendBtnWid, newheight);
    CGRect newScrollFrame = CGRectMake(0, 0, width, height-newContainerFrame.size.height);
    
    
    [self.ChatContainer setFrame:newContainerFrame];
    [self.ChatScroll setFrame:newScrollFrame];
    [self.ChatInput setFrame:newInputFrame];
    [self.ChatSend  setFrame:newSendBtnFrame];
}


-(void)addDialog:(NSString*)sender withText:(NSString*)text{
    //    [self.objects addObject:@{@"sender":sender,@"text":text}];
    //    NSIndexPath *indexPath = [NSIndexPath indexPathForRow:self.objects.count-1 inSection:0];
    //    [self.tableView insertRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
}


-(void)refreshChat{
    for(Message* msg in self.ChatLogs.pendingObjects){
//        NSPredicate *pred = [NSPredicate predicateWithFormat:@"roomAddress = %@ AND datetime = %ld AND idx = %d",msg.roomAddress,msg.datetime,msg.idx];
        
        if(msg.datetime>0){
            [self.ChatLogs.objects addObject:msg];
            [self.ChatLogs.pendingObjects removeObject:msg];
        }
    }
}

//
//-(void)newmsg:(NSNotification*)noti{
//    NSLog(@"%@",noti.userInfo);
//    if(![[noti.userInfo objectForKey:(NSString*)KEY_MSG_TYPE] isEqualToString:(NSString*)VALUE_MSG_TYPE_NEW_MSG_ARRIVAL])
//        return;
//    NSString* address = [noti.userInfo objectForKey:(NSString*)KEY_ADDRESS];
//    if(![address isEqualToString:self.detailItem])
//        return;
//    NSDictionary* json = [CKJsonParser parseJson:[noti.userInfo objectForKey:(NSString*)KEY_MESSAGE_JSON]];
//    [self addDialog:@"you" withText:[json objectForKey:@"message"]];
//    
//    NSString* datetimeStr = [noti.userInfo objectForKey:(NSString*)KEY_SEND_DATETIME];
//    NSString* idxStr = [noti.userInfo objectForKey:(NSString*)KEY_INDEX];
    //    
//    NSString* body = [NSString stringWithFormat:@"%@|%@|%@|",address,datetimeStr,idxStr];
////    sendMessage(0x2111,(uint8_t*)[body cStringUsingEncoding:NSUTF8StringEncoding]);
//
//}

#pragma mark - message manage

- (IBAction)sendButtonClicked:(id)sender {
   if(self.ChatInput.text.length < 1) return;
    Message *msg = [[Message alloc]init];
    msg.text = self.ChatInput.text;
    msg.mine = YES;
    msg.datetime = -1;
    msg.type = @"text";
    msg.roomAddress = self.friend.address;
    
    self.ChatInput.text = nil;
    [self textViewDidChange:self.ChatInput];
    [self.ChatLogs.pendingObjects addObject:msg];
    [self.ChatScroll reloadData];
    [self.ChatScroll scrollToRowAtIndexPath:[self.ChatLogs last] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
    
    [[MessageDispatcher getInstance] sendMessage:msg]
}

         
         
         
         
@end
