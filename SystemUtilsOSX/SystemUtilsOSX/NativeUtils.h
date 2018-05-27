//
//  SystemTrayNative.h
//  SystemTrayOSX
//
//  Created by Tobias on 27.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#include "jni.h"
#import <Cocoa/Cocoa.h>

#ifndef SystemTrayOSX_SystemTrayNative_h
#define SystemTrayOSX_SystemTrayNative_h

long getID(JNIEnv* , jobject);
bool isSameClass(const char *, JNIEnv*, jobject);

@interface NSImage (Java)
+ (NSImage*) getImageFromJava:(jbyteArray) image env:(JNIEnv*) env;
- (NSImage*) resizeImageWithSize:(NSSize) size;
- (jbooleanArray) imageToJava: (JNIEnv*) env;
@end

@interface NSString (Java)
+ (NSString*) stringFromJava: (jstring) string env: (JNIEnv*) env;
@end

#endif
