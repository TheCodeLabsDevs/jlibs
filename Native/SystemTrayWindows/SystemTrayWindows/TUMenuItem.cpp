#include "Stdafx.h"

#include "TUMenus.h"
#include "SystemTrayWindows.h"

using namespace SystemTrayWindows;

TUMenuItem::TUMenuItem(long id) {
	this->id = id;
	this->item = gcnew MenuItem("Item", gcnew EventHandler(this, &TUMenuItem::handler));
	this->item->Tag = id;
}

void TUMenuItem::handler(Object^ sender, EventArgs^ e) {
	jclass listenerClassRef = ENV->GetObjectClass(saved_listener_instance);
	jmethodID listenerEventOccured = ENV->GetMethodID(listenerClassRef, "callListener", "()V");
	ENV->CallVoidMethod(saved_listener_instance, listenerEventOccured);
}

void TUMenuItem::setJava(JNIEnv* env, jobject o) {
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

void TUMenuItem::setTitle(String^ title) {
	this->item->Text = title;
}

MenuItem^ TUMenuItem::getMenuItem() {
	return item;
}
