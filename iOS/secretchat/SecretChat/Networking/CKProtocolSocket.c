//
//  ProtocolSocket.c
//  secretchat
//
//  Created by 김창규 on 2015. 4. 20..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#include "CKProtocolSocket.h"

bool writable = false;
bool readable = false;

CFReadStreamRef read_stream = NULL;
CFWriteStreamRef write_stream = NULL;


void readCallback( CFReadStreamRef stream, CFStreamEventType eventType, void *clientCallBackInfo );
void writeCallback ( CFWriteStreamRef stream, CFStreamEventType eventType, void *clientCallBackInfo );
void sendOpenedNotification();
void receiveData();

void socket_init(CFStringRef host, UInt32 port){
    
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, host, port, &read_stream, &write_stream);

    CFStreamClientContext readContext={0,NULL,NULL,NULL,NULL};
    CFReadStreamSetClient(read_stream,
            kCFStreamEventOpenCompleted|
                    kCFStreamEventHasBytesAvailable|
                    kCFStreamEventErrorOccurred|
                    kCFStreamEventEndEncountered,
            readCallback,
            &readContext);
    
    CFStreamClientContext writesContext={0,NULL,NULL,NULL,NULL};
    CFWriteStreamSetClient(write_stream,
            kCFStreamEventOpenCompleted|
                    kCFStreamEventCanAcceptBytes|
                    kCFStreamEventErrorOccurred|
                    kCFStreamEventEndEncountered,
            writeCallback,
            &writesContext);
    
    
    CFReadStreamScheduleWithRunLoop(read_stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
    CFWriteStreamScheduleWithRunLoop(write_stream, CFRunLoopGetCurrent(), kCFRunLoopDefaultMode);
    
    CFReadStreamOpen(read_stream);
    CFWriteStreamOpen(write_stream);
}

void socket_finalize(){
    if(read_stream)
        CFReadStreamClose(read_stream);
    if(write_stream)
        CFWriteStreamClose(write_stream);
    read_stream=nil;
    write_stream=nil;
}

void readCallback( CFReadStreamRef stream, CFStreamEventType eventType, void *clientCallBackInfo ){
    switch (eventType) {
        case kCFStreamEventOpenCompleted:
            printf("opened\n");
            break;
        case kCFStreamEventHasBytesAvailable:
            receiveData(read_stream);
            printf("can reads!\n");
            break;
        case kCFStreamEventEndEncountered:
            break;
        case kCFStreamEventErrorOccurred:
            socketError();
            break;
        default:
            break;
    }
}

void writeCallback ( CFWriteStreamRef stream, CFStreamEventType eventType, void *clientCallBackInfo ){
    switch (eventType) {
        case kCFStreamEventOpenCompleted:
            printf("write opened\n");

            break;
        case kCFStreamEventCanAcceptBytes:
            printf("can writes!\n");
            if(!writable)
                socketOpened();
            writable = true;
            break;
        case kCFStreamEventEndEncountered:
            socketClosed();
            break;
        case kCFStreamEventErrorOccurred:
            socketError();
            break;
        default:
            break;

    }
}
void tlvComplete(struct tlv_stuct tlvdata){
    bodyparse(tlvdata);
    if(tlvdata.body!=nil)
        free(tlvdata.body);
}
void parseError(uint16_t header){
     printf("err\n");
}

void sendMessage(int header, uint8_t * body){

    long cnt = 0;
    while(body[cnt++]!='\0');
    uint8_t headerAndLength[6] =
    {(uint8_t) ((header&0xff00)>>8),
            (uint8_t) (header&0xff),
            (uint8_t) ((cnt&0xff000000)>>24),
            (uint8_t) ((cnt&0x00ff0000)>>16),
            (uint8_t) ((cnt&0x0000ff00)>>8),
            (uint8_t) (cnt&0x000000ff)};
    long res;
    res = CFWriteStreamWrite(write_stream, headerAndLength, 6);
    res = CFWriteStreamWrite(write_stream, body,cnt);
    
}



