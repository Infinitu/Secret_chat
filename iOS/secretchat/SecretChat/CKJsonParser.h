//
//  JsonParsor.h
//  week3
//
//  Created by 김창규 on 2015. 3. 18..
//  Copyright (c) 2015년 김창규. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CKJsonParser : NSObject

+(id) parseJson:(NSString*)jsonStr;
+ (NSString *)serializeObject:(id)obj;
@end
