//
//  FriendManager.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "FriendManager.h"
#import "ProtocolSocket.h"
#import "FriendRequest.h"
#import "Version.h"
#import "CKJsonParser.h"
#import "Friend.h"
#import "AppDelegate.h"
#import "NotificationManager.h"

@interface FriendManager ()
@property NSOperationQueue *queue;
@end

@implementation FriendManager
FriendManager *FriendManager_instance;

NSString* createRandomInBase64(int byteCnt){
    int in32 = byteCnt /4;
    int last32 = byteCnt %4;
    NSMutableData *data = [NSMutableData dataWithCapacity:(NSUInteger) byteCnt];
    uint32_t t;
    while(in32--){
        t = arc4random();
        [data appendBytes:&t length:4];
    }
    t = arc4random();
    [data appendBytes:&t length:(NSUInteger) last32];
    return [data base64EncodedStringWithOptions:nil];
}

+ (FriendManager *)getInstance {
    if(!FriendManager_instance)
        FriendManager_instance = [[FriendManager alloc] init];
    return FriendManager_instance;
}

-(instancetype)init{
    self = [super init];
    if(!self) return nil;
    _queue = [[NSOperationQueue alloc] init];
    return self;
}

- (void)sendFriendRequestTo:(NSString *)address withMessage:(NSString *)message {
    if(!message)message = @"";
    NSLog(@"%@",[NSString stringWithFormat:@"%@|%@",address,message]);
    sendMessage(0x5001,
            (uint8_t *) [[NSString stringWithFormat:@"%@|%@|",address,message]
                                cStringUsingEncoding:NSUTF8StringEncoding]);
}

- (void)sendFriendResponseTo:(NSString *)address withAction:(NSString *)action {
    if(!action)action = @"deny";
    NSLog(@"%@",[NSString stringWithFormat:@"%@|%@",address,action]);
    sendMessage(0x5002,
            (uint8_t *) [[NSString stringWithFormat:@"%@|%@|",address,action]
                    cStringUsingEncoding:NSUTF8StringEncoding]);
    FriendRequest *req = [FriendRequest objectForPrimaryKey:address];
    [[RLMRealm defaultRealm] beginWriteTransaction];
    [[RLMRealm defaultRealm] deleteObject:req];
    [[RLMRealm defaultRealm] commitWriteTransaction];
}

- (void)messageReceivedFromMatchmaker:(NSString *)messageStr {
    NSDictionary *message = [CKJsonParser parseJson:messageStr];
    NSString *type = message[@"type"];
    if([type isEqualToString:@"friends_requested"]){
        [self requestReceivedWithData:message[@"message"]];
    }
    else if([type isEqualToString:@"established"]){
        [self establishedReceivedWithData:message[@"message"]];


    }
    else if([type isEqualToString:@"established_from_randomroom"]){
        [self establishedInRandomRoomReceivedWithData:message[@"message"]];
    }
}

-(void)requestReceivedWithData:(NSDictionary *)data {
    NSString *address = data[@"address"];
    NSString *message = data[@"message"];
    [self getUserInfomation:address WithCallback:^(NSDictionary *dictionary) {
        FriendRequest *request = [[FriendRequest alloc] init];
        request.profileImage = dictionary[@"imageUrl"];
        request.nickname = dictionary[@"nickName"];
        request.address = address;
        request.message = message;
        request.status = @"waiting";
        [[RLMRealm defaultRealm] beginWriteTransaction];
        [FriendRequest createInDefaultRealmWithValue:request];
        [[RLMRealm defaultRealm] commitWriteTransaction];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"FriendsRequestUpdated" object:self];
        AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
        if([appDelegate isInBackground]){
            [[NotificationManager getInstance] pushNotification:[NSString stringWithFormat:@"%@님이 새로운 친구요청을 보내셨습니다.",request.nickname]
                                                      withTitle:@"새 친구 요청" withAction:@"요청 보기"
                                                   withUserInfo:@{@"msgType":@"friend_request"}];
        }
    }];
}

-(void)establishedReceivedWithData:(NSDictionary *)data {
    NSString *address = data[@"address"];
    NSString *secret = data[@"encKey"];
    [self getUserInfomation:address WithCallback:^(NSDictionary *dictionary) {
        Friend *newFriend = [[Friend alloc]init];
        newFriend.address = address;
        newFriend.age = [dictionary[@"age"] intValue];
        newFriend.profileImg = dictionary[@"imageUrl"];
        newFriend.nickname = dictionary[@"nickName"];
        newFriend.encKey = createRandomInBase64(64);
        newFriend.msgEncKey = data[@"encKey"];
        newFriend.bloodType = dictionary[@"bloodType"];
        newFriend.level = @"UNKNOWN";
        newFriend.sex = [dictionary[@"gender"] intValue];

        [[RLMRealm defaultRealm] beginWriteTransaction];
        [Friend createInDefaultRealmWithValue:newFriend];
        [[RLMRealm defaultRealm] commitWriteTransaction];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"FriendListChanged" object:self];
        AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
        if([appDelegate isInBackground]){
            [[NotificationManager getInstance] pushNotification:[NSString stringWithFormat:@"%@님이 새로운 친구로 등록되었습니다.",newFriend.nickname]
                                                      withTitle:@"Friends Established" withAction:@"메시지 보내기"
                                                   withUserInfo:@{@"msgType":@"friend_established",@"data":newFriend.address}];
        }
    }];
}


-(void)establishedInRandomRoomReceivedWithData:(NSDictionary *)data {
    //todo randomROOOOOOM
}
-(void)getUserInfomation:(NSString*)address WithCallback:(void (^)(NSDictionary *)) block{
    [self getUsersInformation:@[address] WithCallback:^(NSArray *arr){
        if(!arr) block(nil);
        block(arr[0]);
    }];
}

-(void)getUsersInformation:(NSArray*)addresses WithCallback:(void (^)(NSArray *))block{
    NSURL *joinURL =
            [[NSURL alloc] initWithScheme:DEFAULT_API_SCHEME
                                     host:[NSString stringWithFormat:@"%@:%d",DEFAULT_API_HOST,DEFAULT_API_PORT]
                                     path:@"/main"];

    NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:joinURL];

    NSMutableString *str = [NSMutableString string];
    for(NSString *addr in addresses){
        if(str.length>0) [str appendString:@","];
        [str appendString:addr];
    }

        req.HTTPMethod = @"POST";
    [req addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [req setHTTPBody:[[NSString stringWithFormat:@"{\"friends\":\"%@\"}",str] dataUsingEncoding:NSUTF8StringEncoding]];
    [NSURLRequest setAllowsAnyHTTPSCertificate:YES forHost:DEFAULT_API_HOST];
    [NSURLConnection sendAsynchronousRequest:req
                                       queue:[NSOperationQueue mainQueue]
                           completionHandler:^(NSURLResponse *res, NSData *data, NSError *err){
                               NSHTTPURLResponse *response = (NSHTTPURLResponse *) res;
                               if(response.statusCode != 200) return block(nil);
                               NSMutableData *mdata = [NSMutableData dataWithData:data];
                               uint8_t nullChar = '\0';
                               [mdata appendBytes: &nullChar length:1];
                               NSString *result= [NSString stringWithUTF8String:mdata.bytes];;
                               block([CKJsonParser parseJson:result]);
                           }];
}
@end
