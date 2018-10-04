//
//  SystemTrayNative.m
//  SystemTrayOSX
//
//  Created by Tobias on 27.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Foundation/Foundation.h>

#include "SystemTrayNative.h"


long getID(JNIEnv* e , jobject o) {
    jclass class = (*e) -> GetObjectClass(e, o);
    jfieldID field = (*e) -> GetFieldID(e, class, "id", "J");
    
    return (*e) -> GetLongField(e, o, field);
}

bool isSameClass(const char* name, JNIEnv* env, jobject obj) {
    jclass cls = (*env) -> GetObjectClass(env, obj);
    
    jmethodID mid = (*env) -> GetMethodID(env, cls, "getClass", "()Ljava/lang/Class;");
    jobject clsObj = (*env) -> CallObjectMethod(env, obj, mid);
    cls = (*env) -> GetObjectClass(env, clsObj);
    mid = (*env) -> GetMethodID(env, cls, "getName", "()Ljava/lang/String;");
    jstring strObj = (jstring)(*env) -> CallObjectMethod(env, clsObj, mid);
    
    const char* str = (*env) -> GetStringUTFChars(env, strObj, NULL);
    
    bool result = strcmp(str, name) == 0;
    (*env) -> ReleaseStringUTFChars(env, strObj, str);
    return result;
}

@implementation SystemTrayNative

SystemTrayNative* instanze;

@synthesize items, notificationItems;

+ (void)initialize {
    if (instanze == nil) {
        SystemTrayNative* i = [SystemTrayNative new];
        instanze = i;
        [[NSUserNotificationCenter defaultUserNotificationCenter] setDelegate:i];
        [[NSApplication sharedApplication] setDelegate:i];
    }
}

- (instancetype)init {
    self = [super init];
    if (self) {
        items = [NSMutableDictionary new];
        notificationItems = [NSMutableDictionary new];
    }
    return self;
}

+(NSObject *)getItemWithID:(long)ID {
    return [[instanze items] objectForKey:[NSNumber numberWithLong:ID]];
}

+ (void)addObjectWithID:(long)ID object:(NSObject *)object {
    [[instanze items] setObject:object forKey:[NSNumber numberWithLong:ID]];
}

+ (void)removeObjectWithID:(long)ID {
    [[instanze items] removeObjectForKey:[NSNumber numberWithLong:ID]];
}

+ (TUNotification *)getNotificationWithID:(long)ID {
    return [[instanze notificationItems] objectForKey:[NSNumber numberWithLong:ID]];
}

+ (void)addNSUserNotificationWithID:(long)ID userNotification:(TUNotification *)object {
    [[instanze notificationItems] setObject:object forKey:[NSNumber numberWithLong:ID]];
}

+ (void)removeNSUserNotificationWithID:(long)ID {
    [[instanze notificationItems] removeObjectForKey:[NSNumber numberWithLong:ID]];
}

+ (void)clear {
    [[instanze items] removeAllObjects];
    [[instanze notificationItems] removeAllObjects];
}

- (void)userNotificationCenter:(NSUserNotificationCenter *)center didActivateNotification:(NSUserNotification *)notification {
    long ID = [notification.userInfo[@"ID"] longValue];
    [[SystemTrayNative getNotificationWithID:ID] listener];
    [[NSUserNotificationCenter defaultUserNotificationCenter] removeDeliveredNotification:notification];
}

- (BOOL)userNotificationCenter:(NSUserNotificationCenter *)center shouldPresentNotification:(NSUserNotification *)notification {
    return YES;
}

@end

@implementation NSImage (Java)

+ (NSImage *)getImageFromJava:(jbyteArray)imageData env:(JNIEnv *) e {
    int lenght = (*e) -> GetArrayLength(e, imageData);
    jbyte* data = (*e) -> GetByteArrayElements(e, imageData, NULL);
    
    NSData* d = [NSData dataWithBytes: data length:lenght];
    NSImage* image = [[NSImage alloc] initWithData:d];
    
    (*e) -> ReleaseByteArrayElements(e, imageData, data, 0);
    
    return image;
}

- (NSImage *)resizeImageWithSize:(NSSize)size {
    NSRect targetFrame = NSMakeRect(0, 0, size.width, size.height);
    NSImage*  targetImage = [[NSImage alloc] initWithSize:size];
    
    [targetImage lockFocus];
    [self drawInRect:targetFrame fromRect:NSZeroRect operation:NSCompositeCopy fraction:1.0 respectFlipped:YES hints:@{NSImageHintInterpolation:
                                  [NSNumber numberWithInt:NSImageInterpolationLow]}];
    [targetImage unlockFocus];
    
    return targetImage;
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