//
//  BodyInterpreter.h
//  secretchat
//
//  Created by 김창규 on 2015. 4. 24..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#ifndef __secretchat__BodyInterpreter__
#define __secretchat__BodyInterpreter__

#include <stdio.h>

#endif /* defined(__secretchat__BodyInterpreter__) */

#import "TLVParsor.h"
#define KEY_MSG_HEADER      CFSTR("MSG_MSGHEADER")
#define KEY_MSG_TYPE        CFSTR("MSG_TYPE")
#define KEY_CAUSE           CFSTR("CAUSE")
#define KEY_CHUNKED_TYPE    CFSTR("CHUNKED")
#define KEY_LOG_MESSAGE     CFSTR("MESSAGE")

#define KEY_REDIRECT_SERVER_HOST CFSTR("HOST")
#define KEY_REDIRECT_SERVER_PORT CFSTR("PORT")

#define KEY_SEND_DATETIME   CFSTR("DATETIME")
#define KEY_ADDRESS         CFSTR("ADDRESS")
#define KEY_ERRORCODE       CFSTR("ERROR_CODE")
#define KEY_MESSAGE_JSON    CFSTR("MSG_JSON")
#define KEY_INDEX           CFSTR("IDX")
#define KEY_LAST_CHECK      CFSTR("LAST_CHECK")
#define KEY_MSG_CNT         CFSTR("MSG_CNT")

#define KEY_REQUIRED_VERSION    CFSTR("REQUIRED_VERSION")
#define KEY_REQUIRED_PROTOCOL   CFSTR("REQUIRED_PROTOCOL")
#define KEY_REQUESTED_VERSION   CFSTR("REQUESTED_VERSION")
#define KEY_REQUESTED_PROTOCOL  CFSTR("REQUESTED_PROTOCOL")
#define KEY_UPDATE_LINK         CFSTR("UPDATE_LINK")

#define VALUE_MSG_TYPE_PING                         CFSTR("PING")
#define VALUE_MSG_TYPE_PONG                         CFSTR("PONG")
#define VALUE_MSG_TYPE_SEESION_OK                   CFSTR("SESSION_OK")
#define VALUE_MSG_TYPE_SESSION_FAILED               CFSTR("SESSION FAILED")
#define VALUE_MSG_TYPE_DISCONNECT_BY_ANOTHER_CONN   CFSTR("DISCONN_BY_ANOTHER")
#define VALUE_MSG_TYPE_SENDING_SUCCESS              CFSTR("SENDING_SUCCESS")
#define VALUE_MSG_TYPE_SENDING_FAILED               CFSTR("SENDING_FAILED")
#define VALUE_MSG_TYPE_NEW_MSG_ARRIVAL              CFSTR("NEW_MSG")
#define VALUE_MSG_TYPE_CHUNKED_MSG_ARRIVAL          CFSTR("CHUNKED_MSG")
#define VALUE_MSG_TYPE_MISSING_NOTI                 CFSTR("MISSING_NOTI")
#define VALUE_MSG_TYPE_MISSING_MSG                  CFSTR("MISSING_MSG")
#define VALUE_MSG_TYPE_READ_CHECK                   CFSTR("READ_CHECK")
#define VALUE_MSG_TYPE_ENQUEUE_SUCCESS              CFSTR("ENQUEUE_SUCCESS")
#define VALUE_MSG_TYPE_ENQUEUE_FAILED               CFSTR("ENQUEUE_FAILED")
#define VALUE_MSG_TYPE_MATCH_ESTABLISHED            CFSTR("MATCH_ESTABLISHED")
#define VALUE_MSG_TYPE_MATCH_TIMEOUT                CFSTR("MATCH_TIMEOUT")
#define VALUE_MSG_TYPE_FRIENDS_REQ                  CFSTR("MATCH_FRIEND_REQ")

#define VALUE_SESSION_FAILED_CAUSE_AUTH                 CFSTR("AUTH_FAILED")
#define VALUE_SESSION_FAILED_CAUSE_REDIRECT             CFSTR("REDIRECT_SERVER")
#define VALUE_SESSION_FAILED_CAUSE_INTERNAL_SERVER_ERR  CFSTR("INTERNAL_ERROR")
#define VALUE_SESSION_FAILED_CAUSE_BANNED               CFSTR("BANNED")
#define VALUE_SESSION_FAILED_CAUSE_VERSION              CFSTR("VERSION")



#define VALUE_CHUNKED_BEGIN                             CFSTR("BEGIN")
#define VALUE_CHUNKED_CONTINUE                          CFSTR("CONTINUE")
#define VALUE_CHUNKED_END                               CFSTR("END")

#define VALUE_ENQUEUE_FAILED_CAUSE_ALREADY_IN_QUEUE     CFSTR("ALREADY_IN_QUEUE")
#define VALUE_ENQUEUE_FAILED_CAUSE_MATCH_COUNT_EXCESSED CFSTR("MATCH_COUNT_EXCESSED")


void bodyparse(struct tlv_stuct tlv);
void messageComplete(int header, CFDictionaryRef dictionary);
void parseError(uint16_t header);