#include "SystemTrayNative.h"

#import <Cocoa/Cocoa.h>

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_register_1N (JNIEnv * e, jobject o) {
    TUSystemTrayItem* tray = [TUSystemTrayItem addItem:getID(e, o)];
    [SystemTrayNative addObjectWithID:tray.ID object:tray];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_removeItem_1N (JNIEnv * e, jobject o) {
    NSObject* obj = [SystemTrayNative getItemWithID:getID(e, o)];
    if ([obj isKindOfClass:[TUSystemTrayItem class]]) {
        TUSystemTrayItem* tray = (TUSystemTrayItem* ) obj;
        [tray removeItem];
        [SystemTrayNative removeObjectWithID:tray.ID];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_setMenu_1N (JNIEnv * e, jobject o, jobject m) {
    TUMenu* menu = (TUMenu*) [SystemTrayNative getItemWithID:getID(e, m)];
    TUSystemTrayItem* tray = (TUSystemTrayItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    
    tray.item.menu = menu.menu;
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_setImage_1N (JNIEnv * e, jobject o, jbyteArray imageData) {
    NSImage* image = [NSImage getImageFromJava:imageData env:e];
    image = [image resizeImageWithSize:CGSizeMake(17, 17)];
    
    TUSystemTrayItem* item = (TUSystemTrayItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    [[item item] setImage:image];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_setToolTip_1N (JNIEnv * e, jobject o, jstring toolTip) {
    TUSystemTrayItem* item = (TUSystemTrayItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    [item.item setToolTip:[NSString stringFromJava:toolTip env:e]];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_deliverNotification (JNIEnv * e, jobject o, jobject noti, jstring title, jstring subtitle, jstring message, jbyteArray imageData) {
    TUNotification* notification = [[TUNotification alloc] initWithID:getID(e, noti)];
    
    JavaVM* vm;
    (*e) -> GetJavaVM(e, &vm);
    notification.vm = vm;
    notification.object = (*e) -> NewGlobalRef(e, noti);
    [notification initListener];
    
    [notification setTitle:[NSString stringFromJava:title env:e] subtitle:[NSString stringFromJava:subtitle env:e] message:[NSString stringFromJava:message env:e] image:[NSImage getImageFromJava:imageData env:e]];
    [notification deliver];
}