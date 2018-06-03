//
//  AwakeUtilsImpl.c
//  AwakeLib
//
//  Created by Tobias on 30.12.15.
//  Copyright © 2015 Tobias. All rights reserved.
//

#include <stdio.h>

#include <IOKit/pwr_mgt/IOPMLib.h>

#include "de_tobias_utils_util_AwakeUtils.h"
#include "jni.h"

IOReturn success;
IOPMAssertionID assertionID;


JNIEXPORT void JNICALL Java_de_tobias_utils_util_AwakeUtils_lock(JNIEnv *env, jobject obj) {
    CFStringRef reasonForActivity= CFSTR("PlayWall is still active");
    
    success = IOPMAssertionCreateWithName(kIOPMAssertPreventUserIdleDisplaySleep,kIOPMAssertionLevelOn, *&reasonForActivity, &assertionID);
}

JNIEXPORT void JNICALL Java_de_tobias_utils_util_AwakeUtils_unlock(JNIEnv *env, jobject obj) {
    if (success == kIOReturnSuccess) {
        success = IOPMAssertionRelease(assertionID);
        
    }
}