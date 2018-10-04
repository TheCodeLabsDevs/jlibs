
#import <Foundation/Foundation.h>
#include "SystemTrayNative.h"

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTray_startUp_1N (JNIEnv * e, jclass c) {
    [SystemTrayNative initialize];
    NSLog(@"Loaded System Tray for OS X");
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTray_tearDown_1N (JNIEnv * e, jclass c) {
    [SystemTrayNative clear];
    NSLog(@"Cleared native resources");
}