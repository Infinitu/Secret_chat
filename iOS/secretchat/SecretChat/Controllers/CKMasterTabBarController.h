//
//  CKMasterTabBarController.h
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>

@class CKUserData;

@interface CKMasterTabBarController : UITabBarController

@property(nonatomic, strong) CKUserData *userData;
@property UIView* titleView;
@end

