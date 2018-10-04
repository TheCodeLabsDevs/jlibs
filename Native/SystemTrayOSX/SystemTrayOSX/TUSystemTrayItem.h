//
//  TUSystemTrayIcon.h
//  SystemTrayOSX
//
//  Created by Tobias on 25.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface TUSystemTrayItem : NSObject

@property (strong) NSStatusItem* item;

@property () long ID;

+ (id) addItem:(long) ID;
- (void) removeItem;

@end
