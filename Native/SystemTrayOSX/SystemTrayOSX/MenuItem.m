//
//  MenuItem.m
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Foundation/Foundation.h>

#include "SystemTrayNative.h"

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_register_1N (JNIEnv * e, jobject o) {
    if (isSameClass("de.tobias.systemtray.menu.MenuItem", e, o)) {
        TUMenuItem* item = [[TUMenuItem alloc] initWithID:getID(e, o)];
        [SystemTrayNative addObjectWithID:item.ID object:item];
        
        JavaVM* vm;
        (*e) -> GetJavaVM(e, &vm);
        item.javaVM = vm;
        item.javaMenuItem = (*e) -> NewGlobalRef(e, o);
        [item initListener];
    } else if (isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
        TUSubMenu* item = [[TUSubMenu alloc] initWithID:getID(e, o)];
        [SystemTrayNative addObjectWithID:item.ID object:item];
    } else if (isSameClass("de.tobias.systemtray.menu.Menu", e, o)) {
        TUMenu* item = [[TUMenu alloc] initWithID:getID(e, o)];
        [SystemTrayNative addObjectWithID:item.ID object:item];
    }
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_MenuItem_unregisterMenu_1N (JNIEnv * e, jobject o) {
    if ([[SystemTrayNative getItemWithID:getID(e, o)] class] == [TUMenuItem class]) {
        TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, o)];
        (*e)->DeleteGlobalRef(e, item.javaMenuItem);
    }
    [SystemTrayNative removeObjectWithID:getID(e, o)];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setName_1N (JNIEnv * e, jobject o, jstring name) {
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    const char * cName = (*e) -> GetStringUTFChars(e, name, NULL);
    [item setTitle:[NSString stringWithUTF8String:cName]];
    (*e) -> ReleaseStringUTFChars(e, name, cName);
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setEnabled_1N (JNIEnv * e, jobject o, jboolean enabled) {
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    if (!enabled)
        [item.item setTarget:nil];
     else
         [item.item setTarget:item];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setHidden_1N (JNIEnv * e, jobject o, jboolean hidden) {
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    item.item.hidden = hidden;
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setImage_1N (JNIEnv * e, jobject o, jbyteArray imageData) {
    NSImage* image = [NSImage getImageFromJava:imageData env:e];
    image = [image resizeImageWithSize:CGSizeMake(18, 18)];
    
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, o)];
    [[item item] setImage:image];
}
