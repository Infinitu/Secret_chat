//
//  CKChatRoomController.m
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKChatRoomController.h"
#import "CKBodyInterpreter.h"
#import "CKJsonParser.h"
#import "CKChatLogTableDataController.h"
#import "CKMessageDispatcher.h"
@interface CKChatRoomController ()
@property UITextView *ChatInputDump;
@property (weak, nonatomic) IBOutlet UIView *ChatContainer;
@property (weak, nonatomic) IBOutlet UITableView *ChatScroll;
@property (weak, nonatomic) IBOutlet UITextView *ChatInput;
@property (weak, nonatomic) IBOutlet UIButton *ChatSend;
@property CKChatLogTableDataController* ChatLogs;
@property BOOL followUp;
@property RLMRealm *realm;
@property CKFriend *friend;
@end

@implementation CKChatRoomController


#pragma mark - Managing the detail item

- (void)setDetailItem:(id)friend {
    self.friend = friend;
}

- (void)configureView{
    if (self.friend) {
        self.title = self.friend.nickname;
        if(self.realm==nil || ![self.realm.path isEqualToString:[self.friend chatRealmPath]]){
            self.realm = [[CKMessageDispatcher getInstance] chatRealmWithFriend:self.friend];
            self.ChatLogs = [[CKChatLogTableDataController alloc] initWithRealm:self.realm];
        }
    }
}

#pragma mark - lifecycle

- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureView];
    [self layoutInitialize];

    self.ChatScroll.dataSource = self.ChatLogs;
    self.ChatScroll.delegate = self.ChatLogs;
    self.ChatScroll.allowsSelection = false;
    
}
-(void)viewWillAppear:(BOOL)animated{
    [self registerNotification];
    [self reloadAllMessage];
}
-(void)viewWillDisappear:(BOOL)animated{
    [self unregisterNotification];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void) registerNotification{
    NSNotificationCenter *notiCenter =[NSNotificationCenter defaultCenter];
    [notiCenter addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:NULL];
    [notiCenter addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:NULL];
    if(self.friend){
        [[NSNotificationCenter defaultCenter]addObserver:self
                                                selector:@selector(updateMessage:)
                                                    name:self.friend.address
                                                  object:[CKMessageDispatcher getInstance]];
    }
}

-(void) unregisterNotification{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - ui layout

-(void)layoutInitialize{
    self.ChatInput.layer.cornerRadius = 6;
    self.ChatInput.layer.masksToBounds = YES;
    self.ChatInput.layer.borderColor = [[UIColor colorWithRed:((CGFloat)0xC7)/0xFF
                                                        green:((CGFloat)0xC7)/0xFF
                                                         blue:((CGFloat)0xCB)/0xFF alpha:1] CGColor];
    self.ChatInput.layer.borderWidth = 0.5;

    [self setViewLayout:[UIScreen mainScreen].bounds.size];
}

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
    
    
    CGFloat sendBtnWid = 49;
    CGFloat sendBtnHei = self.ChatSend.frame.size.height;

    
    CGFloat newWidth = screen.width-sendBtnWid-30;
    self.ChatInputDump.text = self.ChatInput.text;
    CGSize newSize = [self.ChatInputDump sizeThatFits:CGSizeMake(newWidth, MAXFLOAT)];
    CGFloat newheight = newSize.height;
    
    newheight = newheight>101?101:newheight;
    newheight = newheight<28?28:newheight;
    
    CGRect newContainerFrame = CGRectMake(0, height-newheight-16, screen.width, newheight+16);
    CGRect newInputFrame = CGRectMake(8, 8, screen.width-49-8, newheight);
    CGRect newSendBtnFrame = CGRectMake(screen.width-sendBtnWid,newheight+16-12-sendBtnHei  , 49, sendBtnHei);
    CGRect newScrollFrame = CGRectMake(0, 0, screen.width, height);

    [self setViewLayoutWithNewContainerFrame:newContainerFrame
                              newScrollFrame:newScrollFrame
                               newInputFrame:newInputFrame
                             newSendBtnFrame:newSendBtnFrame];
}

- (void)textViewDidChange:(UITextView *)textView{
    CGFloat height = self.ChatScroll.frame.size.height;
    CGFloat width = self.ChatScroll.frame.size.width;
    
    CGFloat sendBtnWid = self.ChatSend.frame.size.width;
    CGFloat sendBtnHei = self.ChatSend.frame.size.height;
    
    
    CGFloat newWidth = width-sendBtnWid-30;
    
    self.ChatInputDump.text = self.ChatInput.text;
    CGSize newSize = [self.ChatInputDump sizeThatFits:CGSizeMake(newWidth, MAXFLOAT)];
    CGFloat newheight = newSize.height;
    
    newheight = newheight>101?101:newheight;
    newheight = newheight<28?28:newheight;
    
    CGRect newContainerFrame = CGRectMake(0, height-newheight-16, width, newheight+16);
    CGRect newInputFrame = CGRectMake(8, 8, width-49-8, newheight);
    CGRect newSendBtnFrame = CGRectMake(width-49, newheight+16-12-sendBtnHei , 49, sendBtnHei);
    
    [self setViewLayoutWithNewContainerFrame:newContainerFrame
                              newScrollFrame:self.ChatScroll.frame
                               newInputFrame:newInputFrame
                             newSendBtnFrame:newSendBtnFrame];
}


- (void)setViewLayoutWithNewContainerFrame:(CGRect)newContainerFrame
                            newScrollFrame:(CGRect)newScrollFrame
                             newInputFrame:(CGRect)newInputFrame
                           newSendBtnFrame:(CGRect)newSendBtnFrame {
    CGFloat offsetDiff = newContainerFrame.size.height  - self.ChatScroll.contentInset.bottom
    - newScrollFrame.size.height + self.ChatScroll.frame.size.height;
    
    
    [self.ChatScroll setContentInset:UIEdgeInsetsMake(64, 0, newContainerFrame.size.height, 0)];
    [self.ChatContainer setFrame:newContainerFrame];
    [self.ChatScroll setFrame:newScrollFrame];
    [self.ChatInput setFrame:newInputFrame];
    [self.ChatSend  setFrame:newSendBtnFrame];
    
    if(offsetDiff>0){
        CGPoint scrollOffset = self.ChatScroll.contentOffset;
        scrollOffset.y += offsetDiff;
        if(scrollOffset.y <0) scrollOffset.y = 0;
        [self.ChatScroll setContentOffset:scrollOffset];
    }
}


#pragma mark - message manage

- (IBAction)sendButtonClicked:(id)sender {
   if(self.ChatInput.text.length < 1) return;
    CKMessage *msg = [[CKMessage alloc]init];
    msg.text = self.ChatInput.text;
    msg.mine = YES;
    msg.datetime = -CURRENT_SYSTEM_TIME_MILLIS_NOW;
    msg.type = @"text";
    msg.roomAddress = self.friend.address;
    
    [self sendMessage:msg];
}

-(void)sendMessage:(CKMessage *)message{
    CKMessage *sent=[[CKMessageDispatcher getInstance] sendMessage:message toFriend:self.friend];
    
    self.ChatInput.text = nil;
    
    [self textViewDidChange:self.ChatInput];
    
    [self.ChatLogs.pendingObjects addObject:sent];
    [self.ChatScroll reloadData];
    [self.ChatLogs updateScroll:self.ChatScroll];
}

-(void)updateMessage:(NSNotification*)noti{
    CKMessage *msg = noti.userInfo[@"msg"];
    if(!msg) return;
    [self.ChatLogs.pendingObjects removeObject:msg];
    [self.ChatLogs.objects addObject:msg];
    [self.ChatScroll reloadData];
    [self.ChatLogs updateScroll:self.ChatScroll];
}

-(void)reloadAllMessage{
    if(self.realm==nil || ![self.realm.path isEqualToString:[self.friend chatRealmPath]])
        self.realm = [[CKMessageDispatcher getInstance] chatRealmWithFriend:self.friend];
    
    if(!self.ChatLogs)
        self.ChatLogs = [[CKChatLogTableDataController alloc] initWithRealm:self.realm];
    [self.ChatLogs reloadAllMessages];
    [self.ChatLogs updateScroll:self.ChatScroll];
}

@end
