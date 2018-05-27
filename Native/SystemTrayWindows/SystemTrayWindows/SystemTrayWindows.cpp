// Dies ist die Haupt-DLL.

#include "stdafx.h"
#include <stdio.h>
#include <string.h>

#include "SystemTrayWindows.h"

#include "TUMenus.h"

using namespace std;
using namespace SystemTrayWindows;

String^ getStringFromJava(JNIEnv* e, jstring string) {
	const char* stringC = e->GetStringUTFChars(string, NULL);
	String^ s = gcnew String(stringC, 0, strlen(stringC), System::Text::Encoding::UTF8);
	e->ReleaseStringUTFChars(string, stringC);
	return s;
}

void SystemTray::startUp() {
	objects = gcnew Dictionary<long, Object^>();
}

void SystemTray::treaDown() {
	for (int i = 0; i < objects->Count; i++) {
		if (SystemTray::getObject(i)->GetType()->Name == "TUSystemTrayItem")
			((TUSystemTrayItem^)SystemTray::getObject(0))->dismiss();
	}
	objects->Clear();
}

void SystemTray::addObject(long id, Object^ object) {
	objects->Add(id, object);
	Console::WriteLine("C: " + objects[id] + " " + id);
}

long SystemTray::getID(JNIEnv* e, jobject o) {
	jclass clazz = e->GetObjectClass(o);
	jfieldID field = e->GetFieldID(clazz, "id", "J");
	return e->GetLongField(o, field);
}

Object^ SystemTray::getObject(long id){
	return objects[id];
}

void SystemTray::removeObject(long id) {
	objects->Remove(id);
}

bool SystemTray::isSameClass(const char* name, JNIEnv* e, jobject o) {
	jclass cls = e->GetObjectClass(o);
	jmethodID methode = e->GetMethodID(cls, "getClass", "()Ljava/lang/Class;");
	jobject obj = e->CallObjectMethod(o, methode);

	cls = e->GetObjectClass(obj);
	methode = e->GetMethodID(cls, "getName", "()Ljava/lang/String;");
	jstring n = (jstring)e->CallObjectMethod(obj, methode);

	const char* nCS = e->GetStringUTFChars(n, NULL);
	bool same = strcmp(nCS, name) == 0;

	e->ReleaseStringUTFChars(n, nCS);
	return same;
}

Icon^ SystemTray::getIconFromJava(JNIEnv* e, jbyteArray iconData) {
	jbyte* d = e->GetByteArrayElements(iconData, NULL);
	int lenght = e->GetArrayLength(iconData);

	System::IO::MemoryStream^ stream = gcnew System::IO::MemoryStream();
	for (int i = 0; i < lenght; i++) {
		stream->WriteByte(d[i]);
	}
	e->ReleaseByteArrayElements(iconData, d, 0);
	stream->Seek(0, System::IO::SeekOrigin::Begin);
	Image^ image = Image::FromStream(stream);

	Bitmap^ map = gcnew Bitmap(image->Width, image->Height, Imaging::PixelFormat::Format32bppArgb);
	Graphics^ g = Graphics::FromImage(map);
	g->DrawImage(image, 0, 0, image->Width, image->Height);
	return Icon::FromHandle(map->GetHicon());
}