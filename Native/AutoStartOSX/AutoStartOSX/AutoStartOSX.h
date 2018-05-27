//
//  AutoStartOSX.h
//  AutoStartOSX
//
//  Created by Tobias on 10.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "jni.h"

@interface AutoStartOSX : NSObject

+ (BOOL)isLaunchAtStartup: (NSString*) item;
+ (void)toggleLaunchAtStartup: (BOOL) on withItem: (NSString*) item;
+ (LSSharedFileListItemRef)itemRefInLoginItems: (NSString*) item;

@end

@interface NSString (Java)
+ (NSString*) stringFromJava: (jstring) string env: (JNIEnv*) env;
@end
