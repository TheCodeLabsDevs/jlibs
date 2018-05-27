#include "Stdafx.h"

#include "de_tobias_systemtray_SystemTray.h"

#include "SystemTrayWindows.h"
#include "TUMenus.h"

using namespace SystemTrayWindows;

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTray_startUp_1N(JNIEnv * e, jclass c) {
	SystemTray::startUp();
}

JNIEXPORT void JNICALL Java_de_tobias_systemtray_SystemTray_tearDown_1N(JNIEnv * e, jclass c) {
	SystemTray::treaDown();
}