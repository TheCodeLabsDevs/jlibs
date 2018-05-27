#include "Stdafx.h"

#include "TUMenus.h"

using namespace SystemTrayWindows;

TUSystemTrayItem::TUSystemTrayItem(long id) {
	this->id = id;
	this->item = gcnew NotifyIcon();
}

NotifyIcon^ TUSystemTrayItem::getNotifyIcon(){
	return item;
}

void TUSystemTrayItem::setIcon(Icon^ icon){
	this->item->Icon = icon;
}

void TUSystemTrayItem::setToolTip(String^ text) {
	item->Text = text;
}

void TUSystemTrayItem::show() {
	this->item->Visible = true;
}

void TUSystemTrayItem::dismiss() {
	this->item->Visible = false;
}

void TUSystemTrayItem::setMenu(TUMenu^ menu) {
	item->ContextMenu = menu->getConextMenu();
}