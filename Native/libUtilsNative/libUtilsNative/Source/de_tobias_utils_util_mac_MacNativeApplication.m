//
//  Utilities.m
//  SystemTrayOSX
//
//  Created by Tobias on 02.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <AppKit/AppKit.h>
#import <IOKit/pwr_mgt/IOPMLib.h>

#import "de_tobias_utils_util_mac_MacNativeApplication.h"
#import "Utils.h"

IOReturn success;
IOPMAssertionID assertionID;

JNIEXPORT void JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_preventSystemSleep_1N (JNIEnv * env, jclass cls, jboolean on) {
    if (on) {
        CFStringRef reasonForActivity = CFSTR("Program is still active");
        success = IOPMAssertionCreateWithName(kIOPMAssertPreventUserIdleDisplaySleep, kIOPMAssertionLevelOn, *&reasonForActivity, &assertionID);
    } else {
        if (success == kIOReturnSuccess) {
            success = IOPMAssertionRelease(assertionID);
        }
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_setDockIconHidden_1N (JNIEnv * env, jclass class, jboolean hidden) {
    if (hidden) {
        [NSApp setActivationPolicy: NSApplicationActivationPolicyProhibited];
    } else {
        [NSApp setActivationPolicy: NSApplicationActivationPolicyRegular];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_setDockIcon_1N (JNIEnv * env, jclass clazz, jbyteArray imageData) {
    NSImage* image = [NSImage getImageFromJava:imageData env:env];
    [NSApp setApplicationIconImage:image];
}

JNIEXPORT void JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_setDockIconBadge_1N (JNIEnv * env, jclass class, jint i) {
    if (i != 0) {
        [[NSApp dockTile] setBadgeLabel:[NSString stringWithFormat:@"%i", i]];
    } else {
        [[NSApp dockTile] setBadgeLabel:@""];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_setAppearance_1N (JNIEnv * env, jclass clazz, jboolean darkAqua) {
    if (darkAqua) {
        NSApp.appearance = [NSAppearance appearanceNamed:NSAppearanceNameDarkAqua];
    } else {
        NSApp.appearance = [NSAppearance appearanceNamed:NSAppearanceNameAqua];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_showFileInFileViewer_1N (JNIEnv * env, jclass cls, jstring file) {
    NSString* nsFile = [NSString stringFromJava:file env:env];
    [[NSWorkspace sharedWorkspace] selectFile:nsFile inFileViewerRootedAtPath:nsFile];
}

JNIEXPORT jbyteArray JNICALL Java_de_tobias_utils_application_system_impl_MacNativeApplication_getImageForFile_1N (JNIEnv * env, jclass cls, jstring path) {
    NSImage* image = [[NSWorkspace sharedWorkspace] iconForFile:[NSString stringFromJava:path env:env]];
    
    NSRect rect = CGRectMake(0, 0, image.size.width, image.size.height);
    CGImageRef ref = [image CGImageForProposedRect:&rect context:nil hints:nil];
    
    NSBitmapImageRep* bitmap = [[NSBitmapImageRep alloc] initWithCGImage:ref];
    NSData* data = [bitmap representationUsingType:NSBitmapImageFileTypePNG properties:@{}];
    
    CGImageRelease(ref);
    
    if (data) {
        int length = (int) data.length;
        
        signed char rawData[length];
        [data getBytes:&rawData length:length];
        
        jbyteArray result = (*env)->NewByteArray(env, length);
        (*env)->SetByteArrayRegion(env, result, 0, length, rawData);
        return result;
    }
    return NULL;
}
