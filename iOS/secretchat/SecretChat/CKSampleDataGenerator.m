//
//  CKSampleDataGenerator.m
//  SecretChat
//
//  IMPORTANT!!
//  this class is for final exam in NHN NEXT iOS Basic class.
//
//  Created by 김창규 on 2015. 6. 3..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKSampleDataGenerator.h"
#import "CKFriend.h"
#import "CKMessageDispatcher.h"
#import "CKBodyInterpreter.h"
#import "CKJsonParser.h"

CKSampleDataGenerator *samplegen_instance;
@interface CKSampleDataGenerator ()
@property NSArray *sampleReplys;
@end
@implementation CKSampleDataGenerator
+(instancetype)getInstance{
    if(!samplegen_instance)
        samplegen_instance = [[CKSampleDataGenerator alloc]init];
    return samplegen_instance;
}

-(instancetype)init{
    self = [super init];
    if(!self) return nil;

    self.sampleReplys =  @[
            @"어, 안녕",
            @"Apple 짱짱맨",
            @"진저 데려와 진저~!!",
            @"Hello world!",
            @"ByeBye World!",
            @"I am a good boy",
            @"으흥ㅎ",
            @"ㅋㅋㅋㅋㅋㅋㅋㅋㅋ",
            @"ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ",
            @"처음엔 사랑이란게...",
            @"참쉽게 영훤할꺼라...",
            @"그렇게 믿었었는데,\n그렇게 믿었었는데...",
            @"낙타고기 먹지마세여, 메르스에 양보하세여",
            @"인하대 병원에 메르스 환자 있대 조심해!!",
            @"님 코딩 안함? -_-;;",
            @"코딩해 코딩!!",
            @"커밋좀 하시죠...;;;",
            @">_<",
            @"ㅜ_ㅜ",
            @"Do you wanna build a Snowman.xcodeproj",
            @"God tell us the reason",
            @"Youth is wasted on the young",
            @"Sugar~ Yes, me",
            @"널 어쩌면 좋니~~",
            @"널 어쩌면, null 어쩌면 NULL어쩌면 좋니~~?",
            @"위 아래 위위 아래",
            @"#먹스타그램",
            @"먼 산 언저리마저 너를 남기고 돌아서는.. 시간은 그만 돌아서라는데..",
            @"난 왜 널 닮은 목소리마저 가슴에 품고도 같이가자 하지 못했나....",
            @"나는 아무말도 못하고오...",
            @"그댈 안고서~~ 그냥 눈물만흘려~~",
            @"자꾸 눈물이 흘러..",
            @"이대로 영원히 있슬수만 있다면..",
            @"오오 그대여 그대라서 고마워요.",
            @"^^;;;;"];

    
    return self;
}

-(void)cpySampleFriendsListFromBundle:(NSBundle*)bundle{
    if([CKFriend allObjects].count <=0){
        RLMRealm *sampleRealm = [RLMRealm realmWithPath:[bundle pathForResource:@"sample" ofType:@"realm"]];
        RLMRealm *defaultRealm = [RLMRealm defaultRealm];
        [defaultRealm beginWriteTransaction];
        for(CKFriend *fr in [CKFriend allObjectsInRealm:sampleRealm])
            [defaultRealm addOrUpdateObject:[[CKFriend alloc] initWithValue:fr]];
        [defaultRealm commitWriteTransaction];
    }
}

-(void)messageWillSend:(NSString*)msg toAddress:(NSString*)address{
    [[CKMessageDispatcher getInstance] sendSuccess:(long) [[NSDate date] timeIntervalSince1970]*1000];
    int await = arc4random()%5;

    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, await * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        NSString *text = self.sampleReplys[arc4random() % self.sampleReplys.count];
        NSDictionary *param = @{
                (__bridge NSString *) KEY_MESSAGE_JSON : [CKJsonParser serializeObject:@{
                        @"text": text,
                        @"url":@"",
                        @"type":@"text",
                }],
                (__bridge id) KEY_SEND_DATETIME :@([[NSDate date] timeIntervalSince1970]*1000),
                (__bridge id) KEY_INDEX :@(0),
                (__bridge id) KEY_ADDRESS : address
        };
        [[CKMessageDispatcher getInstance] newChatMessage:param];
    });
}

@end
