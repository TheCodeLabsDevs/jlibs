//
//  AutoStartOSX.m
//  AutoStartOSX
//
//  Created by Tobias on 10.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "AutoStartOSX.h"

@implementation AutoStartOSX

+ (BOOL)isLaunchAtStartup: (NSString*) item {
    LSSharedFileListItemRef itemRef = [AutoStartOSX itemRefInLoginItems: item];
    BOOL isInList = itemRef != nil;
    if (itemRef != nil)
        CFRelease(itemRef);
    
    return isInList;
}

+ (void)toggleLaunchAtStartup: (BOOL) on withItem: (NSString*) item {
    LSSharedFileListRef loginItemsRef = LSSharedFileListCreate(NULL, kLSSharedFileListSessionLoginItems, NULL);
    if (loginItemsRef == nil)
        return;
    if (on) {
        CFURLRef appUrl = (__bridge CFURLRef)[NSURL fileURLWithPath:item];
        LSSharedFileListItemRef itemRef = LSSharedFileListInsertItemURL(loginItemsRef, kLSSharedFileListItemLast, NULL, NULL, appUrl, NULL, NULL);
        if (itemRef) CFRelease(itemRef);
    } else {
        LSSharedFileListItemRef itemRef = [AutoStartOSX itemRefInLoginItems: item];
        LSSharedFileListItemRemove(loginItemsRef,itemRef);
        if (itemRef != nil) CFRelease(itemRef);
    }
    CFRelease(loginItemsRef);
}

+ (LSSharedFileListItemRef)itemRefInLoginItems: (NSString*) item {
    LSSharedFileListItemRef res = nil;
    
    NSURL *bundleURL = [NSURL fileURLWithPath:item];
    LSSharedFileListRef loginItemsRef = LSSharedFileListCreate(NULL, kLSSharedFileListSessionLoginItems, NULL);
    if (loginItemsRef == nil) return nil;
    NSArray *loginItems = (__bridge NSArray *)LSSharedFileListCopySnapshot(loginItemsRef, nil);
    for (id item in loginItems) {
        LSSharedFileListItemRef itemRef = (__bridge LSSharedFileListItemRef)(item);
        CFURLRef itemURLRef;
        if (LSSharedFileListItemResolve(itemRef, 0, &itemURLRef, NULL) == noErr) {
            NSURL *itemURL = (__bridge NSURL *)itemURLRef;
            if ([itemURL isEqual:bundleURL]) {
                res = itemRef;
                break;
            }
        }
    }
    if (res != nil)
        CFRetain(res);
    CFRelease(loginItemsRef);
    CFRelease((__bridge CFTypeRef)(loginItems));
    
    return res;
}

@end

@implementation NSString (Java)

+ (NSString*) stringFromJava:(jstring)string env:(JNIEnv *)env {
    const char* cString = (*env)->GetStringUTFChars(env, string, NULL);
    NSString* nsString = [NSString stringWithUTF8String:cString];
    (*env)->ReleaseStringUTFChars(env, string, cString);
    return nsString;
}

@end
