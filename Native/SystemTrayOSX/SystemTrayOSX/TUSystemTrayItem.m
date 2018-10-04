//
//  TUSystemTrayIcon.m
//  SystemTrayOSX
//
//  Created by Tobias on 25.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUSystemTrayItem.h"

@implementation TUSystemTrayItem

@synthesize item;

+ (id)addItem: (long) ID {
    TUSystemTrayItem* trayItem = [TUSystemTrayItem new];
    trayItem.ID = ID;
    trayItem.item = [[NSStatusBar systemStatusBar] statusItemWithLength:NSVariableStatusItemLength];
    trayItem.item.highlightMode = YES;
    return trayItem;
}

- (void)removeItem {
    [[NSStatusBar systemStatusBar] removeStatusItem:item];
}

@end
