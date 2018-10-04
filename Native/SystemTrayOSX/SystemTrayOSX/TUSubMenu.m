//
//  TUSubMenu.m
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUSubMenu.h"

@implementation TUSubMenu

@synthesize item, menu;

- (id)initWithID:(long)i {
    self = [self init];
    if (self) {
        self.ID = i;
    }
    return self;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        menu = [NSMenu new];
        
        item = [[NSMenuItem alloc] init];
        item.submenu = menu;
    }
    return self;
}

-(void)setTitle:(NSString *)string {
    item.title = string;
}

@end
