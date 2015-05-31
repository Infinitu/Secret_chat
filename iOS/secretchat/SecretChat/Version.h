//
//  Version.h
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#ifndef SecretChat_Version_h
#define SecretChat_Version_h


#endif

#define APP_VERSION @"0.0.1"
#define PROTOCOL_VERSION @"1.0"
#define PING_TIMEOUT_IN_SEC 1
#define PING_INTERVAL 2

#define DEFAULT_HOST @"10.0.0.2"
#define DEFAULT_PORT 9000
#define DEFAULT_API_SCHEME @"https"

#define DEFAULT_API_HOST @"10.0.0.2"
#define DEFAULT_API_PORT 7000

@interface NSURLRequest (DummyInterface)
+ (BOOL)allowsAnyHTTPSCertificateForHost:(NSString*)host;
+ (void)setAllowsAnyHTTPSCertificate:(BOOL)allow forHost:(NSString*)host;
@end