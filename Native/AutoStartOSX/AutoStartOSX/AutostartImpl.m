//
//  AutostartImpl.m
//  AutoStartOSX
//
//  Created by Tobias on 10.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "AutoStartOSX.h"
#import "de_tobias_autostart_impl_OSXAutoStartNative.h"

JNIEXPORT void JNICALL Java_de_tobias_autostart_impl_OSXAutoStartNative_add_1N (JNIEnv * e, jobject o, jstring src) {
    [AutoStartOSX toggleLaunchAtStartup:true withItem:[NSString stringFromJava:src env:e]];
}

JNIEXPORT jboolean JNICALL Java_de_tobias_autostart_impl_OSXAutoStartNative_isAutoStart_1N (JNIEnv * e, jobject o, jstring src) {
    return [AutoStartOSX isLaunchAtStartup:[NSString stringFromJava:src env:e]];
}

JNIEXPORT void JNICALL Java_de_tobias_autostart_impl_OSXAutoStartNative_removeAutoStart_1N (JNIEnv * e, jobject o, jstring name) {
    [AutoStartOSX toggleLaunchAtStartup:false withItem:[NSString stringFromJava:name env:e]];
}

