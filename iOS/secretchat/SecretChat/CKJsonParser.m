//
//  JsonParsor.m
//  week3
//
//  Created by 김창규 on 2015. 3. 18..
//  Copyright (c) 2015년 김창규. All rights reserved.
//

#import "CKJsonParser.h"

@implementation CKJsonParser

+(id) parseJson:(NSString*)jsonStr{
    int idx = 0;
    return [CKJsonParser _parseJson:[jsonStr cStringUsingEncoding:NSUTF8StringEncoding]
                          withIdx:&idx withlength:(int)jsonStr.length];
}

+(id) _parseJson:(const char*)str withIdx:(int*)idx withlength:(int)length{
    if(str[*idx] == '{')
        return [CKJsonParser _parseJsonObject:str withIdx:idx withlength:length];
    if(str[*idx] == '[')
        return [CKJsonParser _parseJsonArray:str withIdx:idx withlength:length];
    if(str[*idx] == '"')
        return [CKJsonParser _parseJsonString:str withIdx:idx withlength:length];
    if(str[*idx] >= '0' && str[*idx] <= '9')
        return [CKJsonParser _parseJsonNumber:str withIdx:idx withlength:length];
    
    
    return nil;
}

+(id) _parseJsonArray:(const char*)str withIdx:(int*)idx withlength:(int)length{
    *idx = *idx+1;
    NSMutableArray *mArray = [NSMutableArray array];
    while (str[*idx] != ']'){
        if(str[*idx] == '\0')
            return nil; //err
        if(!(str[*idx] == ',' || str[*idx] == ' ' || str[*idx] == '\n'))
            [mArray addObject: [CKJsonParser _parseJson:str withIdx:idx withlength:length]];
        else
            *idx = *idx + 1;
    }
    *idx = *idx + 1;
    return mArray;
}


+(id) _parseJsonObject:(const char*)str withIdx:(int*)idx withlength:(int)length{
    *idx = *idx+1;
    NSMutableDictionary *mDictionary = [NSMutableDictionary dictionary];
    int state = 1;
    //1 : finding key;
    //2 : finding value;
    
    id key = NULL;
    id value = NULL;
    
    while (str[*idx] != '}' ){
        if(str[*idx] == '\0')
            return nil; //err
        
        if(str[*idx] == ':')
            state = 2; // make finding value;
        else if(str[*idx] == ','){
            [mDictionary addEntriesFromDictionary: [NSDictionary dictionaryWithObject:value forKey:key]];
            state = 1;
        }
        else if(str[*idx] == ' ' || str[*idx] == '\n'){
            //do nothing
        }
        else if(state == 1){//finding key
            key = [CKJsonParser _parseJsonString:str withIdx:idx withlength:length];
            continue;
        }
        else if(state == 2){ //finding value
            value = [CKJsonParser _parseJson:str withIdx:idx withlength:length];
            continue;
        }
        *idx = *idx+1;
    }
    
    [mDictionary addEntriesFromDictionary: [NSDictionary dictionaryWithObject:value forKey:key]];
    *idx = *idx+1;
    return mDictionary;
}

+(id) _parseJsonString:(const char*)str withIdx:(int*)idx withlength:(int)length{
    *idx = *idx+1;
    int started = *idx;
    
    while (str[*idx] != '"' ){
        if(str[*idx] == '\0')
            return nil; //err
        *idx = *idx+1;
    }
    int jsonStrLength =  *idx-started;
    if(length == 0)
        return NULL;
    *idx = *idx+1;
    char copy[jsonStrLength+1];
    strncpy(copy,&str[started], jsonStrLength);
    copy[jsonStrLength] = '\0';
    
    return [NSString stringWithCString:copy encoding:NSUTF8StringEncoding];
}

+(id) _parseJsonNumber:(const char*)str withIdx:(int*)idx withlength:(int)length{
    
    int started = *idx;
    
    while(str[*idx] >= '0' && str[*idx]<='9'){
        if(str[*idx] == '\0')
            return nil; //err
        *idx = *idx+1;
    }
    int jsonStrLength =  *idx-started;
    if(length == 0)
        return NULL;
    
    char copy[jsonStrLength+1];
    strncpy(copy,&str[started], jsonStrLength);
    copy[jsonStrLength] = '\0';
    
    return [NSNumber numberWithLong:
            [[NSString stringWithCString:copy encoding:NSUTF8StringEncoding] integerValue] ];

}


+ (NSString *)serializeObject:(id)obj{
    
    if([obj isKindOfClass:[NSArray class]]){
        NSMutableString *str = [NSMutableString string];
        [str appendString: @"["];
        NSString *format = @"%@";
        for(id k in (NSArray*)obj){
            [str appendFormat:format,[self serializeObject:k]];
            format = @",%@";
        }
        [str appendString: @"]"];
        return str;
    }
    
    if([obj isKindOfClass:[NSDictionary class]]){
        NSMutableString *str = [NSMutableString string];
        NSDictionary *dic = (NSDictionary*)obj;
        [str appendString: @"{"];
        NSString *format = @"%@:%@";
        for(NSString* k in dic){
            [str appendFormat:format,[self serializeObject:k],[self serializeObject:[dic objectForKey:k]]];
            format = @",%@:%@";
        }
        [str appendString: @"}"];
        return str;
    }
    
    if([obj isKindOfClass:[NSString class]]){
        return [NSString stringWithFormat:@"\"%@\"",(NSString*)obj];
    }
    
    if([obj isKindOfClass:[NSNumber class]]){
        return [obj description];
    }
    return nil;
}


@end
