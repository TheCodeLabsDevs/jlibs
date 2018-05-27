#include "Stdafx.h"

#include "de_tobias_systemtray_menu_MenuItem.h"

#include "SystemTrayWindows.h"
#include "TUMenus.h"

using namespace SystemTrayWindows;

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_register_1N(JNIEnv * e, jobject o) {
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.Menu", e, o)) {
		TUMenu^ menu = gcnew TUMenu(SystemTray::getID(e, o));
		SystemTray::addObject(menu->id, menu);
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
		TUSubMenu^ menu = gcnew TUSubMenu(SystemTray::getID(e, o));
		SystemTray::addObject(menu->id, menu);
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, o)) {
		TUMenuItem^ menu = gcnew TUMenuItem(SystemTray::getID(e, o));
		menu->setJava(e, o);
		SystemTray::addObject(menu->id, menu);
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_unregisterMenu_1N(JNIEnv * e, jobject o) {
	SystemTray::removeObject(SystemTray::getID(e, o));
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setName_1N(JNIEnv * e, jobject o, jstring name) {
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, o));
		item->setTitle(getStringFromJava(e, name));
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, o)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, o));
		item->setTitle(getStringFromJava(e, name));
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setEnabled_1N(JNIEnv * e, jobject o, jboolean enabled) {
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, o));
		item->getMenuItem()->Enabled = enabled;
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, o)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, o));
		item->getMenuItem()->Enabled = enabled;
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setHidden_1N(JNIEnv * e, jobject o, jboolean hidden) {
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, o));
		item->getMenuItem()->Visible = !hidden;
	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, o)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, o));
		item->getMenuItem()->Visible = !hidden;
	}
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_menu_MenuItem_setImage_1N(JNIEnv * e, jobject o, jbyteArray imageData) {
	if (SystemTray::isSameClass("de.tobias.systemtray.menu.SubMenu", e, o)) {
		TUSubMenu^ item = (TUSubMenu^)SystemTray::getObject(SystemTray::getID(e, o));

	}
	else if (SystemTray::isSameClass("de.tobias.systemtray.menu.MenuItem", e, o)) {
		TUMenuItem^ item = (TUMenuItem^)SystemTray::getObject(SystemTray::getID(e, o));

	}
}