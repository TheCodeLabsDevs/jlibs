package de.tobias.systemtray.menu;

import de.thecodelabs.utils.util.ImageUtils;
import de.tobias.systemtray.ActionListener;
import de.tobias.systemtray.Utilities;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.HashMap;

public class MenuItem {

	private long id;
	private String name;
	private boolean enabled;
	private boolean hidden;
	private MenuItem parent;
	private ActionListener actionListener;

	private HashMap<String, Object> userInfo;

	public MenuItem(String name) {
		id = Utilities.getNewID();
		userInfo = new HashMap<>();

		register_N();

		setName(name);
	}

	@Override
	protected void finalize() throws Throwable {
		unregisterMenu_N();
	}

	public HashMap<String, Object> getUserInfo() {
		return userInfo;
	}

	public MenuItem getParent() {
		return parent;
	}

	protected void setParent(MenuItem item) {
		this.parent = item;
	}

	public void removeFromParent() {
		if (parent != null) {
			if (parent instanceof Menu) {
				Menu menu = (Menu) parent;
				menu.removeItem(this);
			} else if (parent instanceof SubMenu) {
				SubMenu menu = (SubMenu) parent;
				menu.removeItem(this);
			}
		}
	}

	// Name
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		setName_N(name);
	}

	// Enable
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		setEnabled_N(enabled);
	}

	// Hidden
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		setHidden_N(hidden);
	}

	public void setImage(Image image) throws IOException {
		setImage_N(ImageUtils.imageToByteArray(image));
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	private void callListener() {
		if (actionListener != null) {
			actionListener.onAction(this);
		}
	}

	private native void register_N();

	private native void unregisterMenu_N();

	private native void setName_N(String name);

	private native void setEnabled_N(boolean enabled);

	private native void setHidden_N(boolean hidden);

	private native void setImage_N(byte[] data);

	protected static MenuItem separator() {
		return new MenuItem("-");
	}
}
