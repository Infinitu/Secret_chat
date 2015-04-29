//
//  ProtocolSocket.c
//  secretchat
//
//  Created by 김창규 on 2015. 4. 20..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#include "ProtocolSocket.h"


bool writable = false;
bool readable = false;

CFReadStreamRef read_stream = NULL;
CFWriteStreamRef write_stream = NULL;


void readCallback( CFReadStreamRef stream, CFStreamEventType eventType, void *clientCallBackInfo );
void writeCallback ( CFWriteStreamRef stream, CFStreamEventType eventType, void *clientCallBackInfo );
void sendOpenedNotification();
void receiveData();


CFNotificationCenterRef notiCenter;

void socket_init(){
    
    notiCenter = CFNotificationCenterGetLocalCenter();
    
    CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, CFSTR("localhost"), 9000    , &read_stream, &write_stream);

    CFStreamClientContext readContext={0,NULL,NULL,NULL,NULL};
    CFReadStreamSetClient(read_stream, kCFStreamEventOpenCompleted|kCFStreamEventHasBytesAvailable|kCFStreamEventErrorOccurred|kCFStreamEventEndEncountered,
                          readCallback, &readContext);
    
    CFStreamClientContext writesContext={0,NULL,NULL,NULL,NULL};
    CFWriteStreamSetClient(write_stream, kCFStreamEventOpenCompleted|kCFStreamEventCanAcceptBytes|kCFStreamEventErrorOccurred|kCFStreamEventEndEncountered,
                           writeCallback, &writesContext);
    
    
    CFReadStreamScheduleWithRunLoop(read_stream, CFRunLoopGetMain(), kCFRunLoopDefaultMode);
    CFWriteStreamScheduleWithRunLoop(write_stream, CFRunLoopGetMain(), kCFRunLoopDefaultMode);
    
    CFReadStreamOpen(read_stream);
    CFWriteStreamOpen(write_stream);
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
                sendOpenedNotification();
            writable = true;
            break;
        default:
            break;

    }
}

void sendOpenedNotification(){
    CFNotificationCenterPostNotification(notiCenter, CFSTR("opened"), NULL, NULL, false);
}

void tlvComplete(struct tlv_stuct tlvdata){
    bodyparse(tlvdata);
    if(tlvdata.body!=nil)
        free(tlvdata.body);
}

void messageComplete(CFDictionaryRef dictionary){
    CFNotificationCenterPostNotification(notiCenter, CFSTR("newmsg"), NULL, dictionary, true);
}
void parseError(uint16_t header){
     printf("err\n");
}

void sendMessage(int header, CFStringRef bodyRef){

    uint8_t *body = (uint8_t*)CFStringGetCStringPtr(bodyRef, kCFStringEncodingUTF8);
    long cnt = 0;
    while(body[cnt++]!='\0');
    uint8_t headerAndLength[6] =
    {((header&0xff00)>>8),
         (header&0xff),
        ((cnt&0xff000000)>>24),
        ((cnt&0x00ff0000)>>16),
        ((cnt&0x0000ff00)>>8),
        (cnt&0x000000ff)};
    long res;
    res = CFWriteStreamWrite(write_stream, headerAndLength, 6);
    printf("%ld\n",res);
    res = CFWriteStreamWrite(write_stream, body,cnt);
    printf("%ld\n",res);
    
    
    
    
    
}



