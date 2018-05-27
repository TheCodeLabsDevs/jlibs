#include "Stdafx.h"
#include "de_tobias_utils_util_SystemUtils.h"

#include <iostream>

#using <System.dll>
#using <System.Drawing.dll>

using namespace System;
using namespace System::Drawing;
using namespace System::Runtime::InteropServices;

String^ getStringFromJava(JNIEnv* e, jstring string) {
	const char* stringC = e->GetStringUTFChars(string, NULL);
	String^ s = gcnew String(stringC, 0, strlen(stringC), System::Text::Encoding::UTF8);
	e->ReleaseStringUTFChars(string, stringC);
	return s;
}

JNIEXPORT jbyteArray JNICALL Java_de_tobias_utils_util_SystemUtils_getImageForFile_1N(JNIEnv * e, jclass c, jstring p) {
	String^ path = getStringFromJava(e, p);

	if (System::IO::File::Exists(path)) {
		Icon^ icon = Icon::ExtractAssociatedIcon(path);
		Image^ image = icon->ToBitmap();
		System::IO::MemoryStream^ stream = gcnew System::IO::MemoryStream();

		image->Save(stream, Imaging::ImageFormat::Png);
		stream->Seek(0, System::IO::SeekOrigin::Begin);

		jbyte* b = new jbyte[stream->Length];

		int i = 0;
		while (i < stream->Length) {
			b[i] = stream->ReadByte();
			i++;
		}
		jbyteArray array = e->NewByteArray(stream->Length);
		e->SetByteArrayRegion(array, 0, stream->Length, b);
		return array;
	}
	else {
		return NULL;
	}
}