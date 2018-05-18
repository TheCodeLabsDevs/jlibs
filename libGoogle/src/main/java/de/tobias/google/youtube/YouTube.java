package de.tobias.google.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube.Thumbnails.Set;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.*;
import de.tobias.google.Authentication;
import de.tobias.google.Service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class YouTube extends Service {

	private com.google.api.services.youtube.YouTube youtube;

	@Override
	protected void init(Authentication authentication, String appName) {
		youtube = new com.google.api.services.youtube.YouTube.Builder(authentication.getHTTP_TRANSPORT(), authentication.getJSON_FACTORY(),
				authentication.getCredential()).setApplicationName(appName).build();
	}

	// https://developers.google.com/youtube/v3/docs/playlistItems/list?hl=de
	public List<Video> getVideos() throws IOException {
		List<Video> videos = new ArrayList<>();
		com.google.api.services.youtube.YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
		channelRequest.setMine(true);
		channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
		ChannelListResponse channelResult = channelRequest.execute();

		List<Channel> channelsList = channelResult.getItems();

		if (channelsList != null) {
			String uploadPlaylistId = channelsList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

			com.google.api.services.youtube.YouTube.PlaylistItems.List playlistItemRequest = youtube.playlistItems().list(
					"id,contentDetails,snippet,status");
			playlistItemRequest.setPlaylistId(uploadPlaylistId);

			playlistItemRequest
					.setFields("items(contentDetails/videoId,snippet(title,description,publishedAt,thumbnails),status/privacyStatus),nextPageToken,pageInfo");

			String nextToken = "";
			do {
				playlistItemRequest.setPageToken(nextToken);
				PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

				playlistItemResult.getItems().forEach(item -> videos.add(new Video(item)));
				nextToken = playlistItemResult.getNextPageToken();
			} while (nextToken != null);
		}
		return videos;
	}

	// https://developers.google.com/youtube/v3/code_samples/java#update_a_video
	public Video updateVideo(Video video) throws IOException {
		com.google.api.services.youtube.YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet,status")
				.setId(video.getVideoID());
		VideoListResponse listResponse = listVideosRequest.execute();
		List<com.google.api.services.youtube.model.Video> videoList = listResponse.getItems();
		if (videoList.isEmpty()) {
			return null;
		}

		com.google.api.services.youtube.model.Video v = videoList.get(0);
		VideoSnippet snippet = v.getSnippet();
		VideoStatus status = v.getStatus();

		snippet.setTitle(video.getTitle());
		snippet.setDescription(video.getDescription());
		status.setPrivacyStatus(video.getStatus().getType());

		// Update the video resource by calling the videos.update() method.
		com.google.api.services.youtube.YouTube.Videos.Update updateVideosRequest = youtube.videos().update("snippet,status", v);
		com.google.api.services.youtube.model.Video videoResponse = updateVideosRequest.execute();
		return new Video(videoResponse);
	}

	public void deleteVideo(Video video) throws IOException {
		youtube.videos().delete(video.getVideoID()).execute();
	}

	// https://developers.google.com/youtube/v3/docs/thumbnails/set
	public String setThumbnail(Path image, String videoId, MediaHttpUploaderProgressListener listenser) {
		try {
			File imageFile = image.toFile();
			InputStreamContent mediaContent = new InputStreamContent("image/png", new BufferedInputStream(new FileInputStream(imageFile)));
			mediaContent.setLength(imageFile.length());

			Set thumbnailSet = youtube.thumbnails().set(videoId, mediaContent);
			MediaHttpUploader uploader = thumbnailSet.getMediaHttpUploader();
			uploader.setDirectUploadEnabled(false);

			uploader.setProgressListener(listenser);

			ThumbnailSetResponse setResponse = thumbnailSet.execute();
			return setResponse.getItems().get(0).getDefault().getUrl();
		} catch (GoogleJsonResponseException e) {
			System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
			e.printStackTrace();

		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	// https://code.google.com/p/youtube-api-samples/source/browse/samples/java/youtube-cmdline-uploadvideo-sample/src/main/java/com/google/api
	// /services/samples/youtube/cmdline/youtube_cmdline_uploadvideo_sample/UploadVideo.java
	public Video uploadVideo(Path videoPath, MediaHttpUploaderProgressListener listener, Video video, List<String> tags) {
		try {
			File videoFile = videoPath.toFile();
			com.google.api.services.youtube.model.Video videoObjectDefiningMetadata = new com.google.api.services.youtube.model.Video();

			VideoStatus status = new VideoStatus();
			status.setPrivacyStatus(video.getStatus().getType());
			videoObjectDefiningMetadata.setStatus(status);

			VideoSnippet snippet = new VideoSnippet();
			snippet.setTitle(video.getTitle());
			snippet.setDescription(video.getDescription());
			snippet.setTags(tags);

			videoObjectDefiningMetadata.setSnippet(snippet);

			InputStreamContent mediaContent = new InputStreamContent("video/*", new BufferedInputStream(new FileInputStream(videoFile)));
			mediaContent.setLength(videoFile.length());

			com.google.api.services.youtube.YouTube.Videos.Insert videoInsert = youtube.videos().insert("snippet,statistics,status",
					videoObjectDefiningMetadata, mediaContent);

			MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();
			uploader.setDirectUploadEnabled(false);
			uploader.setProgressListener(listener);

			com.google.api.services.youtube.model.Video returnedVideo = videoInsert.execute();
			return new Video(returnedVideo);
		} catch (GoogleJsonResponseException e) {
			System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			System.err.println("Throwable: " + t.getMessage());
			t.printStackTrace();
		}
		return null;
	}

	public static String[] scope = {YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_UPLOAD};

}
