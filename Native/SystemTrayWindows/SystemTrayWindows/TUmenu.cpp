#include "Stdafx.h"

#include "TUMenus.h"
using namespace SystemTrayWindows;

TUMenu::TUMenu(long id) {
	this->id = id;
	this->menu = gcnew ContextMenu();
}

ContextMenu^ TUMenu::getConextMenu() {
	return this->menu;
}

void TUMenu::addItem(MenuItem^ item) {
	this->menu->MenuItems->Add(item);
}

void TUMenu::insertItem(MenuItem^ item, int index) {
	this->menu->MenuItems->Add((Int32)index, item);
}

void TUMenu::removeItem(MenuItem^ item) {
	this->menu->MenuItems->Remove(item);
}

void TUMenu::removeItem(int index) {
	this->menu->MenuItems->RemoveAt(index);
}

void TUMenu::clearMenu() {
	this->menu->MenuItems->Clear();
}