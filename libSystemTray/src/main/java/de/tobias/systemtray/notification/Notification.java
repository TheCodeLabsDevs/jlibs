package de.tobias.systemtray.notification;

import de.tobias.systemtray.ActionListener;
import de.tobias.systemtray.Utilities;

public final class Notification {

	private long id;
	private byte[] image;
	private String title = "";
	private String subTitle = "";
	private String message = "";
	private ActionListener listener;

	public Notification() {
		this.id = Utilities.getNewID();
	}

	public Notification(String message) {
		this();
		this.message = message;
	}

	public Notification(String title, String message) {
		this(message);
		this.title = title;
	}

	public Notification(String title, String subtitle, String message) {
		this(title, message);
		this.subTitle = subtitle;
	}

	public long getId() {
		return id;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subTitle;
	}

	public void setSubtitle(String subtitle) {
		this.subTitle = subtitle;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ActionListener getListener() {
		return listener;
	}

	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	protected void callListener() {
		if (listener != null) {
			listener.onAction(this);
		}
	}
}
