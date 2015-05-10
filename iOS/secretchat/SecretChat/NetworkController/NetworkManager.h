//
//  NetworkManager.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UserData.h"


@interface NetworkManager : NSObject

+(void)initializeWithUserData:(UserData*)userData withHost:(NSString*)host withPort:(NSInteger)port;
@end
