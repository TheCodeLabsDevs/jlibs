//
//  TUNotification.h
//  SystemTrayOSX
//
//  Created by Tobias on 09.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "SystemTrayNative.h"

@interface TUNotification: NSObject

@property () long ID;

@property (strong) NSUserNotification* notification;
@property () JavaVM* vm;
@property () jobject object;
@property () jmethodID methodeID;

- initWithID: (long) ID;

- (void) setTitle: (NSString*) title subtitle: (NSString*) subtitle message: (NSString*) message image: (NSImage*) image;

- (void) initListener;
- (void) listener;

- (void) deliver;
@end
