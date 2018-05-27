#include "Stdafx.h"

#include "de_tobias_systemtray_menu_Menu.h"

#include "SystemTrayWindows.h"
#include "TUMenus.h"

using namespace SystemTrayWindows;

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_addItem_1N(JNIEnv * e, jobject o, jobject menuItem, jboolean separator) {
	TUMenu^ menu = (TUMenu^)SystemTray::getObject(SystemTray::getID(e, o));
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, menuItem)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, menuItem));
		menu->addItem(item->getMenuItem());
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, menuItem)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, menuItem));
		menu->addItem(item->getMenuItem());
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_insertItem_1N(JNIEnv * e, jobject o, jobject menuItem, jint index, jboolean separator) {
	TUMenu^ menu = (TUMenu^)SystemTray::getObject(SystemTray::getID(e, menuItem));
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, menuItem));
		menu->insertItem(item->getMenuItem(), index);
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, menuItem)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, menuItem));
		menu->insertItem(item->getMenuItem(), index);
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_removeItem_1N__Lde_tobias_systemtray_menu_MenuItem_2(JNIEnv * e, jobject o, jobject i) {
	TUMenu^ menu = (TUMenu^)SystemTray::getObject(SystemTray::getID(e, o));
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, i)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, i));
		menu->removeItem(item->getMenuItem());
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, i)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, i));
		e->DeleteGlobalRef(item->saved_listener_instance);
		menu->removeItem(item->getMenuItem());
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_removeItem_1N__I(JNIEnv * e, jobject o, jint index) {
	TUMenu^ menu = (TUMenu^)SystemTray::getObject(SystemTray::getID(e, o));
	menu->removeItem(index);
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_Menu_clearMenu_1N(JNIEnv * e, jobject o) {
	TUMenu^ menu = (TUMenu^)SystemTray::getObject(SystemTray::getID(e, o));
	menu->clearMenu();
}