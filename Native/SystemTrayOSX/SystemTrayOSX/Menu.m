//
//  Menu.m
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Foundation/Foundation.h>

#include "SystemTrayNative.h"

#pragma mark add items

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_addItem_1N (JNIEnv * e, jobject o, jobject menuItem, jboolean sepatator) {
    TUMenu* menu = (TUMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, menuItem)];
    [menu.menu addItem:item.item];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_insertItem_1N (JNIEnv * e, jobject o, jobject menuItem, jint index, jboolean sepatator) {
    TUMenu* menu = (TUMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, menuItem)];
    [menu.menu insertItem:item.item atIndex:index];
}

#pragma mark remove items

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_removeItem_1N (JNIEnv * e, jobject o, jobject menuItem) {
    TUMenu* menu = (TUMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    TUMenuItem* item = (TUMenuItem*) [SystemTrayNative getItemWithID:getID(e, menuItem)];
    (*e)->DeleteGlobalRef(e, item.javaMenuItem);
    [menu.menu removeItem:item.item];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_removeItemAtIndex_1N (JNIEnv * e, jobject o, jint index) {
    TUMenu* menu = (TUMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    [menu.menu removeItemAtIndex:index];
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_clearMenu_1N (JNIEnv * e, jobject o) {
    TUMenu* menu = (TUMenu*) [SystemTrayNative getItemWithID:getID(e, o)];
    [menu.menu removeAllItems];
}
