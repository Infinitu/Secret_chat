//
//  CKSampleDataGenerator.h
//  SecretChat
//
//  Created by 김창규 on 2015. 6. 3..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CKSampleDataGenerator : NSObject
+(instancetype)getInstance;
-(void)cpySampleFriendsListFromBundle:(NSBundle*)bundle;
-(void)messageWillSend:(NSString*)msg toAddress:(NSString*)address;
@end
