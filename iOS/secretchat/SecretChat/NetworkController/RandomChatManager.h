//
// Created by 김창규 on 15. 5. 31..
// Copyright (c) 2015 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface RandomChatManager : NSObject
@property BOOL isInQueue;

+(instancetype)getInstance;
-(void)enqueueSuccessfully;
-(void)dequeueSuccessfully;
-(void)matchEstablished:(NSDictionary*)dictionary;
-(void)failed:(NSDictionary*)dictionary;


void randomEnqueu();
void randomDequeu();
@end