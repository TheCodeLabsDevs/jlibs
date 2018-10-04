//
//  SubMenu.m
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Foundation/Foundation.h>

#include "SystemTrayNative.h"

#pragma mark add items

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_SubMenu_addItem_1N (JNIEnv * e, jobject o, jobject menuItem, jboolean sepatator) {
    TUSubMenu* menu = (TUSubMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, menuItem)];
    [menu.menu addItem:item.item];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_SubMenu_insertItem_1N (JNIEnv * e, jobject o, jobject menuItem, jint index, jboolean sepatator) {
    TUSubMenu* menu = (TUSubMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, menuItem)];
    [menu.menu insertItem:item.item atIndex:index];
}

#pragma mark remove items

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_SubMenu_removeItem_1N (JNIEnv * e, jobject o, jobject menuItem) {
    TUSubMenu* menu = (TUSubMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, menuItem)];
    (*e)->DeleteGlobalRef(e, item.javaMenuItem);
    [menu.menu removeItem:item.item];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_SubMenu_removeItemAtIndex_1N (JNIEnv * e, jobject o, jint index) {
    TUSubMenu* menu = (TUSubMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    [menu.menu removeItemAtIndex:index];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_SubMenu_clearMenu_1N (JNIEnv * e, jobject o) {
    TUSubMenu* menu = (TUSubMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    [menu.menu removeAllItems];
}