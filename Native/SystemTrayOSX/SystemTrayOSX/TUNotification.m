//
//  TUNotification.m
//  SystemTrayOSX
//
//  Created by Tobias on 09.10.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUNotification.h"

@implementation TUNotification

JNIEnv* g_env;

@synthesize notification, vm, object, methodeID, ID;

- (id)initWithID:(long)ID {
    self = [super init];
    if (self) {
        notification = [[NSUserNotification alloc] init];
    }
    return self;
}

- (void)setTitle:(NSString *)title subtitle:(NSString *)subtitle message:(NSString *)message image:(NSImage *)image {
    notification.title = title;
    notification.subtitle = subtitle;
    notification.informativeText = message;
    [notification setValue: image forKey:@"_identityImage"];
    notification.userInfo = @{@"ID": [NSNumber numberWithLong:self.ID]};
}

- (void)initListener {
    int getEnvStat = (*vm) -> GetEnv(vm, (void **)&g_env, JNI_VERSION_1_8);
    if (getEnvStat == JNI_EDETACHED) {
        printf("GetEnv: not attached\n");
        if ((*vm) -> AttachCurrentThread(vm, (void **) &g_env, NULL) != 0) {
            printf("Failed to attach\n");
        }
    }
    jclass g_clazz = (*g_env) -> GetObjectClass(g_env, object);
    methodeID = (*g_env) -> GetMethodID(g_env, g_clazz, "callListener", "()V");
}

- (void) listener {
    (*g_env) -> CallVoidMethod(g_env, object, methodeID);
}

- (void)deliver {
    [SystemTrayNative addNSUserNotificationWithID:self.ID userNotification:self];
    [[NSUserNotificationCenter defaultUserNotificationCenter] deliverNotification:notification];
}

@end
