#include "Stdafx.h"

#include "TUMenus.h"
using namespace SystemTrayWindows;

void TUNotification::handler(Object^ sender, EventArgs^ e) {
	jclass listenerClassRef = ENV->GetObjectClass(saved_listener_instance);
	jmethodID listenerEventOccured = ENV->GetMethodID(listenerClassRef, "callListener", "()V");
	ENV->CallVoidMethod(saved_listener_instance, listenerEventOccured);

	icon->BalloonTipClicked -= h;
	icon->BalloonTipClosed -= hc;
}

void TUNotification::handlerC(Object^ sender, EventArgs^ e) {
	icon->BalloonTipClicked -= h;
	icon->BalloonTipClosed -= hc;
}

TUNotification::TUNotification(long id) {
	this->id = id;
}

void TUNotification::show(String^ title, String^ message, TUSystemTrayItem^ item) {
	item->getNotifyIcon()->ShowBalloonTip(5, title, message, ToolTipIcon::Info);

	h = gcnew EventHandler(this, &TUNotification::handler);
	icon = item->getNotifyIcon();
	item->getNotifyIcon()->BalloonTipClicked += h;
	hc = gcnew EventHandler(this, &TUNotification::handlerC);
	item->getNotifyIcon()->BalloonTipClosed += hc;
}

void TUNotification::setJava(JNIEnv* env, jobject o) {
	JavaVM* vm;
	JNIEnv* e;

	env->GetJavaVM(&vm);
	saved_listener_instance = env->NewGlobalRef(o);

	int stat = vm->GetEnv((void **)&e, JNI_VERSION_1_8);
	if (stat == JNI_EDETACHED)
		vm->AttachCurrentThread((void **)&e, NULL);

	savedVM = vm;
	this->ENV = e;
}