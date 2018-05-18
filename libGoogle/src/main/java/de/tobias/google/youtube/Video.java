package de.tobias.google.youtube;

import com.google.api.services.youtube.model.PlaylistItem;

public class Video {

	public enum Status {
		PUBLIC("public"),
		PRIVATE("private"),
		UNLISTED("unlisted");
		private String type;

		private Status(String type) {
			this.type = type;
		}

		public static Status getStatus(String type) {
			for (Status status : Status.values()) {
				if (status.type.equals(type)) {
					return status;
				}
			}
			return null;
		}

		public String getType() {
			return type;
		}
	}

	private String videoID;
	private String title;
	private String description;
	private String url;
	private String thumbnailURL;
	private String status;

	public Video(String title, String description, Status status) {
		this.title = title;
		this.description = description;
		this.status = status.type;
	}

	public Video(PlaylistItem item) {
		videoID = item.getContentDetails().getVideoId();
		title = item.getSnippet().getTitle();
		status = item.getStatus().getPrivacyStatus();
		description = item.getSnippet().getDescription();
		url = "https://www.youtube.de/watch?v=" + item.getContentDetails().getVideoId();
		try {
			thumbnailURL = item.getSnippet().getThumbnails().getHigh().getUrl();
		} catch (Exception e) {
			System.err.println("No High Thumbnail for video: " + videoID + ": " + title);
			thumbnailURL = item.getSnippet().getThumbnails().getDefault().getUrl();
		}
	}

	public Video(com.google.api.services.youtube.model.Video returnedVideo) {
		videoID = returnedVideo.getId();
		title = returnedVideo.getSnippet().getTitle();
		status = returnedVideo.getStatus().getPrivacyStatus();
		description = returnedVideo.getSnippet().getDescription();
		url = "https://www.youtube.de/watch?v=" + returnedVideo.getId();
		try {
			thumbnailURL = returnedVideo.getSnippet().getThumbnails().getHigh().getUrl();
		} catch (Exception e) {
			System.err.println("No High Thumbnail for video: " + videoID);
			thumbnailURL = returnedVideo.getSnippet().getThumbnails().getDefault().getUrl();
		}
	}

	public String getVideoID() {
		return videoID;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Status getStatus() {
		return Status.getStatus(status);
	}

	public String getUrl() {
		return url;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStatus(Status status) {
		this.status = status.type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return videoID + "\t" + title;
	}
}
