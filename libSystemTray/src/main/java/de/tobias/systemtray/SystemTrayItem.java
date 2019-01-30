package de.tobias.systemtray;

import de.thecodelabs.utils.util.ImageUtils;
import de.tobias.systemtray.menu.Menu;
import de.tobias.systemtray.notification.Notification;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SystemTrayItem {

	private long id;
	private Image image;
	private Menu menu;
	private String toolTip;
	private ActionListener listener;

	private List<Notification> notifications;

	public SystemTrayItem() {
		id = Utilities.getNewID();
		notifications = new ArrayList<>();

		register_N();
	}

	public void removeItem() {
		register_N();
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) throws IOException {
		this.image = image;
		setImage_N(ImageUtils.imageToByteArray(image));
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
		setMenu_N(menu);
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
		setToolTip_N(toolTip);
	}

	public void deliverNotification(Notification notification) throws IOException {
		notification.setImage(ImageUtils.imageToByteArray(getImage()));
		notifications.add(notification);

		deliverNotification(notification, notification.getTitle(), notification.getSubtitle(), notification.getMessage(),
				notification.getImage());
	}

	private native void register_N();

	private native void removeItem_N();

	private native void setMenu_N(Menu menu);

	private native void setImage_N(byte[] image);

	private native void setToolTip_N(String toolTip);

	private native void deliverNotification(Notification notification, String title, String subtitle, String message, byte[] imageData);
}
