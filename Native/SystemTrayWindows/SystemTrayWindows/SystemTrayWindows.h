// SystemTrayWindows.h

#pragma once

#include <jni.h>

using namespace System;
using namespace System::Collections::Generic;

String^ getStringFromJava(JNIEnv*, jstring);

namespace SystemTrayWindows {

	public ref class SystemTray : Object
	{
	private:
		static Dictionary<long, Object^>^ objects;
	public:
		static void startUp();
		static void treaDown();
		static void addObject(long id, Object^ object);
		static long getID(JNIEnv* e, jobject o);
		static Object^ getObject(long id);
		static void removeObject(long id);
		static bool isSameClass(const char* name, JNIEnv* e, jobject o);
		static Icon^ getIconFromJava(JNIEnv*, jbyteArray imageData);
	};
}
