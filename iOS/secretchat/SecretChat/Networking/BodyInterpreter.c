//
//  BodyInterpreter.c
//  secretchat
//
//  Created by 김창규 on 2015. 4. 24..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#include "BodyInterpreter.h"
#include <stdarg.h>

CFStringRef* buildKVList(int cnt, ...);
int splitWithPipe(CFStringRef** result, uint8_t* body, long length);

void bodyparse(struct tlv_stuct tlv){
    int kvlen;
    const CFStringRef *keys=nil;
    const CFStringRef *values=nil;
    CFStringRef *param=nil;
    CFNumberRef headnum = CFNumberCreate(kCFAllocatorDefault, kCFNumberIntType, &tlv.header);
    switch (tlv.header) {
        case 0x0001: // ping
            kvlen = 1;
            keys = buildKVList(kvlen,KEY_MSG_HEADER,KEY_MSG_TYPE);
            values = buildKVList(kvlen,headnum,VALUE_MSG_TYPE_PING);
            break;
        case 0x0002: // pong
            kvlen = 1;
            keys = buildKVList(kvlen,KEY_MSG_HEADER,KEY_MSG_TYPE);
            values = buildKVList(kvlen,headnum,VALUE_MSG_TYPE_PONG);
            break;
        case 0x1002: // session okay
            kvlen = 1;
            keys = buildKVList(kvlen,KEY_MSG_HEADER,KEY_MSG_TYPE);
            values = buildKVList(kvlen,headnum,VALUE_MSG_TYPE_SEESION_OK);
            break;
        case 0x1003: // redirect server
            kvlen = 5;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 3){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_CAUSE,
                               KEY_REDIRECT_SERVER_HOST,
                               KEY_REDIRECT_SERVER_PORT,
                               KEY_LOG_MESSAGE);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SESSION_FAILED,
                                 VALUE_SESSION_FAILED_CAUSE_REDIRECT,
                                 param[0],
                                 param[1],
                                 param[2]);
            break;
        case 0x1004: // auth failed
            kvlen = 3;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 1){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_CAUSE,
                               KEY_LOG_MESSAGE);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SESSION_FAILED,
                                 VALUE_SESSION_FAILED_CAUSE_AUTH,
                                 param[0]);
            break;
        case 0x1005: // internal server err
            kvlen = 3;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 1){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_CAUSE,
                               KEY_LOG_MESSAGE);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SESSION_FAILED,
                                 VALUE_SESSION_FAILED_CAUSE_INTERNAL_SERVER_ERR,
                                 param[0]);
            break;
        case 0x1006: // banned user.
            kvlen = 3;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 1){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_CAUSE,
                               KEY_LOG_MESSAGE);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SESSION_FAILED,
                                 VALUE_SESSION_FAILED_CAUSE_AUTH,
                                 param[0]);
            break;
        case 0x1007: // client version is not permited.
            kvlen = 7;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 5){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_CAUSE,
                               KEY_REQUESTED_VERSION,
                               KEY_REQUESTED_PROTOCOL,
                               KEY_REQUIRED_VERSION,
                               KEY_REQUIRED_PROTOCOL,
                               KEY_UPDATE_LINK);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SESSION_FAILED,
                                 VALUE_SESSION_FAILED_CAUSE_AUTH,
                                 param[0],
                                 param[1],
                                 param[2],
                                 param[3],
                                 param[4]);
            break;
        case 0x1101: // disconnected by another connection.
            kvlen = 2;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 1){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_LOG_MESSAGE);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_DISCONNECT_BY_ANOTHER_CONN,
                                 param[0]);
            break;
        case 0x2012: // seding message successful.
            kvlen = 2;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 1){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_SEND_DATETIME,
                               KEY_INDEX);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SENDING_SUCCESS,
                                 param[0],
                                 param[1]);
            break;
        case 0x2013: // seding message failed.
            kvlen = 2;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 1){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_SEND_DATETIME);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_SENDING_FAILED,
                                 param[0]);
            break;
        case 0x2101: // new message arrival
            kvlen = 5;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 4){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_ADDRESS,
                               KEY_SEND_DATETIME,
                               KEY_INDEX,
                               KEY_MESSAGE_JSON);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_NEW_MSG_ARRIVAL,
                                 param[0],
                                 param[1],
                                 param[2],
                                 param[3]);
            break;
        case 0x2102: // new chunked message arrival begin
            //todo
            break;
        case 0x2103: // new chunked message arrival continue
            //todo
            break;
        case 0x2104: // new chunked message arrival end
            //todo
            break;
        case 0x3001: // missing message notification.
            kvlen = 5;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 4){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_MSG_CNT);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_MISSING_NOTI,
                                 param[0]);
            break;
        case 0x3003: // missing message
            kvlen = 5;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 4){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_ADDRESS,
                               KEY_SEND_DATETIME,
                               KEY_INDEX,
                               KEY_MESSAGE_JSON);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_MISSING_MSG,
                                 param[0],
                                 param[1],
                                 param[2],
                                 param[3]);
            break;
        case 0x3102: // message read check.
            kvlen = 3;
            if(splitWithPipe(&param,tlv.body, tlv.length) < 2){
                parseError(tlv.header);
                goto errorBreak;
            }
            keys = buildKVList(kvlen,
                               KEY_MSG_HEADER,
                               KEY_MSG_TYPE,
                               KEY_ADDRESS,
                               KEY_LAST_CHECK);
            
            values = buildKVList(kvlen,
                                 headnum,
                                 VALUE_MSG_TYPE_READ_CHECK,
                                 param[0],
                                 param[1]);
            break;
        case 0x4101: // enqueue successful.
            //todo
            break;
        case 0x4111: // enqueue failed : already in queue
            //todo
            break;
        case 0x4112: // enqueue failed : match count limit excessed.
            //todo
            break;
        case 0x4201: // match established
            //todo
            break;
        case 0x4202: // match timeout
            //todo
            break;
        case 0x4302: // friends request in random room.
            //todo
            break;
        default:
            parseError(tlv.header);
            break;
    }
    
    CFDictionaryRef dict = CFDictionaryCreate(kCFAllocatorDefault,(const void**)keys,(const void**)values, kvlen, NULL,NULL);
    messageComplete(tlv.header, dict);
errorBreak:
    if(keys!=nil)
        free((void*)keys);
    if(values!=nil)
        free((void*)values);
    if(param!=nil)
        free(param);
    return;
}

CFStringRef* buildKVList(int cnt, ...){
    
    va_list ap;
    CFStringRef c;
    va_start(ap, cnt);
    
    CFStringRef *clist = malloc(sizeof(CFStringRef)*cnt);
    int idx = 0;
    while(cnt-- ) {
        c = va_arg(ap, CFStringRef);
        clist[idx++] = c;
    }
    va_end(ap);
    
    return clist;
}

int splitWithPipe(CFStringRef** result, uint8_t* body, long length){
    if(length<=0)
        return 0;
    body[length] = (uint8_t)'\0';
    long idx = length;
    int cnt = (body[length-1]=='|')?0:1;
    while(idx--){
        if(body[idx]=='|'){
            body[idx] = '\0';
            cnt++;
        }
    }
    
    CFStringRef* slist = malloc(sizeof(CFStringRef*)*cnt);
    idx = 0;
    for(int i = 0; i<cnt; i++){
        long st = idx;
        long len = 0;
        for(;body[len+st]!='\0';len++);
        slist[i] = CFStringCreateWithBytes(kCFAllocatorDefault, &body[st], len, kCFStringEncodingUTF8, true);
        idx += len+1;
    }
    
    *result = slist;
    return cnt;
}
