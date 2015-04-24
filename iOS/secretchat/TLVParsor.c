//
//  TLVParsor.c
//  secretchat
//
//  Created by 김창규 on 2015. 4. 22..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#include "TLVParsor.h"

uint8_t buff[SOCKET_BUFF_SIZE];


int state = 1;

uint32_t fragmentSize;
struct tlv_stuct parseData;

void receiveBuffer(uint8_t *ptr, long length);

bool isParsing = false;

void receiveData(CFReadStreamRef stream){
    if(isParsing)
        return;
    isParsing = true;
    long len;
    while((len = CFReadStreamRead(stream, buff, SOCKET_BUFF_SIZE))>0)
        receiveBuffer(buff, len);
    isParsing=false;
}

void receiveBuffer(uint8_t *ptr, long length){
    if(length <=0)
        return;
    switch(state){
        case STATE_HEADER:
            if(fragmentSize>0){
                parseData.header = (parseData.header & 0xff00) | *ptr;
                length --;
                ptr = &ptr[1];
            }
            else if(length==1){
                parseData.header = ((ptr[0] << 8) & 0xff00);
                length--;
                return;
            }
            else {
                parseData.header = ((ptr[0] << 8) & 0xff00) | ptr[1];
                length-=2;
                ptr = &ptr[2];
            }
            state = STATE_LENGTH;
            fragmentSize = 0;
            return receiveBuffer(ptr, length);
            break;
        case STATE_LENGTH:
            if(fragmentSize==0){
                parseData.length = 0;
            }
            for(;length>0 && fragmentSize<4;fragmentSize++){
                parseData.length = ((parseData.length << 8)&0xffffffff00)|*ptr;
                ptr = &ptr[1];
                length--;
            }
            if(fragmentSize>=4){
                state = STATE_BODY;
                fragmentSize = 0;
            }
            return receiveBuffer(ptr, length);
            break;
        case STATE_BODY:
            if(fragmentSize == 0){
                parseData.body = malloc(sizeof(uint8_t)*length+1);
            }
            long needs = parseData.length - fragmentSize;
            needs = needs>length?length:needs;
            memcpy(&parseData.body[fragmentSize], ptr, needs);
            fragmentSize += needs;
            length -= needs;
            ptr = &ptr[needs];
            if(fragmentSize >= parseData.length){
                tlvComplete(parseData);
                parseData.body = nil;
                state = STATE_HEADER;
                fragmentSize = 0;
            }
            return receiveBuffer(ptr, length);
            
            break;
    }
}

