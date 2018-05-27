//
//  Impl.m
//  SystemUtilsOSX
//
//  Created by Tobias on 25.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Cocoa/Cocoa.h>
#import "NativeUtils.h"
#import "de_tobias_utils_util_SystemUtils.h"

JNIEXPORT jbyteArray JNICALL Java_de_tobias_utils_util_SystemUtils_getImageForFile_1N (JNIEnv * e, jclass c, jstring path) {
    NSString * file = [NSString stringFromJava:path env:e];
    NSImage* image = [[NSWorkspace sharedWorkspace] iconForFile:file];
    return [image imageToJava:e];
}