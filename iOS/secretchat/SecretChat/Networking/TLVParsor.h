//
//  TLVParsor.h
//  secretchat
//
//  Created by 김창규 on 2015. 4. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#ifndef __secretchat__TLVParsor__
#define __secretchat__TLVParsor__

#include <stdio.h>

#endif /* defined(__secretchat__TLVParsor__) */

#import <CoreFoundation/CoreFoundation.h>


#define SOCKET_BUFF_SIZE 1024
#define STATE_HEADER 1
#define STATE_LENGTH 2
#define STATE_BODY 3

struct tlv_stuct{
    uint16_t header;
    uint32_t length;
    uint8_t  *body;
};

void receiveData(CFReadStreamRef stream);
void tlvComplete(struct tlv_stuct tlvdata);