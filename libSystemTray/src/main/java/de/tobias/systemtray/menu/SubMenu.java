package de.tobias.systemtray.menu;

import java.util.ArrayList;
import java.util.List;

public class SubMenu extends MenuItem {

	private List<MenuItem> items;

	public SubMenu() {
		this("");
	}

	public SubMenu(String name) {
		super(name);
		this.items = new ArrayList<MenuItem>();
	}

	public List<MenuItem> getItems() {
		return items;
	}

	public void addItem(MenuItem menu) {
		addItem_N(menu, false);
		items.add(menu);
		menu.setParent(this);
	}

	public void insertItem(MenuItem menu, int index) {
		insertItem_N(menu, index, false);
		items.add(index, menu);
		menu.setParent(this);
	}

	public MenuItem addSeparator() {
		MenuItem separator = MenuItem.separator();
		addItem_N(separator, true);
		separator.setParent(this);
		return separator;
	}

	public MenuItem insertSeperator(int index) {
		MenuItem separator = MenuItem.separator();
		insertItem_N(separator, index, true);
		separator.setParent(this);
		return separator;
	}

	public void removeItem(MenuItem menu) {
		removeItem_N(menu);
		items.remove(menu);
	}

	public void removeItem(int index) {
		removeItemAtIndex_N(index);
		items.remove(index);
	}

	public void clearMenu() {
		clearMenu_N();
		items.clear();
	}

	private native void addItem_N(MenuItem menu, boolean separator);

	private native void insertItem_N(MenuItem menu, int index, boolean separator);

	private native void removeItem_N(MenuItem menu);

	private native void removeItemAtIndex_N(int index);

	private native void clearMenu_N();

}
