//
//  TUMenu.m
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUMenu.h"

@implementation TUMenu

@synthesize menu;

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
        menu = [[NSMenu alloc] initWithTitle:@"Menu"];
    }
    return self;
}

-(void)setTitle:(NSString *)string {
    menu.title = string;
}

@end
