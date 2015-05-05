//
//  ProtocolSocket.h
//  secretchat
//
//  Created by 김창규 on 2015. 4. 20..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#ifndef __secretchat__ProtocolSocket__
#define __secretchat__ProtocolSocket__

#include <stdio.h>

#endif /* defined(__secretchat__ProtocolSocket__) */

#import <CoreFoundation/CoreFoundation.h>

#include "BodyInterpreter.h"

#define NOTIFICATION_NAME_OPENED @"SOCKET_OPENED"
#define NOTIFICATION_NAME_CLOSED @"SOCKET_CLOSED"
#define NOTIFICATION_NAME_NEWMSG @"SOCKET_NEWMSG"
#define NOTIFICATION_NAME_PINGTO @"SOCKET_PINGTO"

enum socket_status{
    CONNECTED,
    DISCONNECTED,
    cWAITING
};

enum socket_status status;
void socket_init();
void sendMessage(int header, uint8_t * body);