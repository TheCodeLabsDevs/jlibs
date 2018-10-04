//
//  SystemTrayNative.h
//  SystemTrayOSX
//
//  Created by Tobias on 27.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUSystemTrayItem.h"
#import "TUMenuItem.h"
#import "TUMenu.h"
#import "TUSubMenu.h"
#import "TUNotification.h"

#include "jni.h"

#import "de_tobias_systemtray_menu_Menu.h"
#import "de_tobias_systemtray_menu_MenuItem.h"
#import "de_tobias_systemtray_menu_SubMenu.h"
#import "de_tobias_systemtray_SystemTray.h"
#import "de_tobias_systemtray_SystemTrayItem.h"

#ifndef SystemTrayOSX_SystemTrayNative_h
#define SystemTrayOSX_SystemTrayNative_h

long getID(JNIEnv* , jobject);
bool isSameClass(const char *, JNIEnv*, jobject);

@class TUNotification;

@interface SystemTrayNative : NSObject <NSUserNotificationCenterDelegate, NSApplicationDelegate>

@property (strong) NSMutableDictionary* items;
@property (strong) NSMutableDictionary* notificationItems;

+ (NSObject* ) getItemWithID: (long) ID;
+ (void) addObjectWithID: (long) ID object: (NSObject*) object;
+ (void) removeObjectWithID: (long) ID;

+ (TUNotification* ) getNotificationWithID: (long) ID;
+ (void) addNSUserNotificationWithID: (long) ID userNotification: (TUNotification*) object;
+ (void) removeNSUserNotificationWithID: (long) ID;

+ (void) clear;

@end

@interface NSImage (Java)
+ (NSImage*) getImageFromJava:(jbyteArray) image env:(JNIEnv*) env;
- (NSImage*) resizeImageWithSize:(NSSize) size;
@end

@interface NSString (Java)
+ (NSString*) stringFromJava: (jstring) string env: (JNIEnv*) env;
@end

#endif
