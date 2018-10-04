//
//  TUSubMenu.h
//  SystemTrayOSX
//
//  Created by Tobias on 26.09.14.
//  Copyright (c) 2014 Tobias. All rights reserved.
//

#import "TUMenuItem.h"

@interface TUSubMenu : TUMenuItem

@property (strong) NSMenuItem* item;
@property (strong) NSMenu* menu;

@end
