#include "Stdafx.h"

#include "TUMenus.h"
using namespace SystemTrayWindows;

TUSubMenu::TUSubMenu(long id) {
	this->id = id;
	this->menu = gcnew MenuItem("SubItem");
}

MenuItem^ TUSubMenu::getMenuItem() {
	return menu;
}

void TUSubMenu::setTitle(String^ title) {
	menu->Text = title;
}

void TUSubMenu::addItem(MenuItem^ item) {
	this->menu->MenuItems->Add(item);
}

void TUSubMenu::insertItem(MenuItem^ item, int index) {
	this->menu->MenuItems->Add((Int32)index, item);
}

void TUSubMenu::removeItem(MenuItem^ item) {
	this->menu->MenuItems->Remove(item);
}

void TUSubMenu::removeItem(int index) {
	this->menu->MenuItems->RemoveAt(index);
}

void TUSubMenu::clearMenu() {
	this->menu->MenuItems->Clear();
}