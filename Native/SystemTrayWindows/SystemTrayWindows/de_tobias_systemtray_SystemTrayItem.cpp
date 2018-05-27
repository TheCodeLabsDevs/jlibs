#include "Stdafx.h"

#include "de_tobias_systemtray_SystemTrayItem.h"

#include "SystemTrayWindows.h"
#include "TUMenus.h"
#include <iostream>

using namespace SystemTrayWindows;
using namespace System::Text;

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_register_1N(JNIEnv * e, jobject o) {
	TUSystemTrayItem^ item = gcnew TUSystemTrayItem(SystemTray::getID(e, o));
	SystemTray::addObject(SystemTray::getID(e, o), item);
	item->show();
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_removeItem_1N(JNIEnv * e, jobject o){
	TUSystemTrayItem^ item = (TUSystemTrayItem^)SystemTray::getObject(SystemTray::getID(e, o));
	item->dismiss();
	SystemTray::removeObject(item->id);
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_setMenu_1N(JNIEnv * e, jobject o, jobject menu) {
	TUMenu^ m = (TUMenu^)SystemTray::getObject(SystemTray::getID(e, menu));
	TUSystemTrayItem^ item = (TUSystemTrayItem^)SystemTray::getObject(SystemTray::getID(e, o));

	item->setMenu(m);
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_setImage_1N(JNIEnv * e, jobject o, jbyteArray iconData) {
	Icon^ icon = SystemTray::getIconFromJava(e, iconData);
	TUSystemTrayItem^ item = (TUSystemTrayItem^)SystemTray::getObject(SystemTray::getID(e, o));
	item->setIcon(icon);
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_setToolTip_1N(JNIEnv * e, jobject o, jstring tooltip) {
	TUSystemTrayItem^ item = (TUSystemTrayItem^)SystemTray::getObject(SystemTray::getID(e, o));
	item->setToolTip(getStringFromJava(e, tooltip));
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTrayItem_deliverNotification(JNIEnv * e, jobject o, jobject noti, jstring title, jstring subtitle, jstring message, jbyteArray imageData) {
	TUNotification^ notification = gcnew TUNotification(SystemTray::getID(e, noti));
	notification->setJava(e, noti);
	notification->show(getStringFromJava(e, title), getStringFromJava(e, message), ((TUSystemTrayItem^)SystemTray::getObject(SystemTray::getID(e, o))));
}