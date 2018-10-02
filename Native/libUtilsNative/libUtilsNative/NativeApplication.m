//
//  Utilities.m
//  SystemTrayOSX
//
//  Created by Tobias on 02.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <AppKit/AppKit.h>
#import "de_tobias_utils_util_mac_NativeApplication.h"
#import "Utils.h"

JNIEXPORT void JNICALL Java_de_tobias_utils_util_mac_NativeApplication_setDockIconHidden_1N (JNIEnv * e, jclass class, jboolean hidden) {
    if (hidden) {
        [NSApp setActivationPolicy: NSApplicationActivationPolicyProhibited];
    } else {
        [NSApp setActivationPolicy: NSApplicationActivationPolicyRegular];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_util_mac_NativeApplication_setDockIcon (JNIEnv * env, jclass clazz, jbyteArray imageData) {
    NSImage* image = [NSImage getImageFromJava:imageData env:env];
    [NSApp setApplicationIconImage:image];
}

JNIEXPORT void JNICALL Java_de_tobias_utils_util_mac_NativeApplication_setDockIconBadge_1N (JNIEnv * e, jclass class, jint i) {
    if (i != 0) {
        [[NSApp dockTile] setBadgeLabel:[NSString stringWithFormat:@"%i", i]];
    } else {
        [[NSApp dockTile] setBadgeLabel:@""];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_util_mac_NativeApplication_setAppearance_1N (JNIEnv * env, jclass clazz, jboolean darkAqua) {
    if (darkAqua) {
        NSApp.appearance = [NSAppearance appearanceNamed:NSAppearanceNameDarkAqua];
    } else {
        NSApp.appearance = [NSAppearance appearanceNamed:NSAppearanceNameAqua];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_utils_util_mac_NativeApplication_showFileInFileViewer_1N (JNIEnv * env, jclass cls, jstring file) {
    NSString* nsFile = [NSString stringFromJava:file env:env];
    [[NSWorkspace sharedWorkspace] selectFile:nsFile inFileViewerRootedAtPath:nsFile];
}
