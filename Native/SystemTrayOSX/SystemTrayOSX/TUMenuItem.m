//
//  TUMenuItem.m
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUMenuItem.h"
#include "de_tobias_systemtray_SystemTray.h"

#import <Carbon/Carbon.h>

@implementation TUMenuItem

JNIEnv* g_env;

@synthesize item, ID, javaMenuItem, javaVM, methode;

- (id)initWithID:(long) i {
    self = [self init];
    if (self) {
        self.ID = i;
    }
    return self;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        item = [[NSMenuItem alloc] initWithTitle:@"Item" action:@selector(listener:) keyEquivalent:@""];
        item.target = self;
    }
    return self;
}

- (void) initListener {
    int getEnvStat = (*javaVM) -> GetEnv(javaVM, (void **)&g_env, JNI_VERSION_1_8);
    if (getEnvStat == JNI_EDETACHED) {
        printf("GetEnv: not attached\n");
        if ((*javaVM) -> AttachCurrentThread(javaVM, (void **) &g_env, NULL) != 0) {
            printf("Failed to attach\n");
        }
    }
    jclass g_clazz = (*g_env) -> GetObjectClass(g_env, javaMenuItem);
    methode = (*g_env) -> GetMethodID(g_env, g_clazz, "callListener", "()V");
}

- (void) listener: (id) sender {
    (*g_env) -> CallVoidMethod(g_env, javaMenuItem, methode);
}

- (void)setTitle:(NSString *)string {
    if ([string isEqualToString:@"-"]) {
        item = [NSMenuItem separatorItem];
    } else {
        item.title = string;
    }
}

@end
