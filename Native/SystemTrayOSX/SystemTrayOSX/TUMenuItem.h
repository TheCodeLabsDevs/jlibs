//
//  TUMenuItem.h
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#include "jni.h"

@interface TUMenuItem : NSObject

@property (strong) NSMenuItem* item;
@property () long ID;
@property () JavaVM* javaVM;
@property () jobject javaMenuItem;
@property () jmethodID methode;

- initWithID: (long) ID;

- (void) setTitle: (NSString* ) string;
- (void) initListener;

@end
