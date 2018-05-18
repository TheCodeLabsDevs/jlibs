package de.tobias.google.drive;

import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.Drive.Files.Update;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import de.tobias.google.Authentication;
import de.tobias.google.Service;
import de.tobias.utils.util.FileUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Drive extends Service {

	private class DownloadFile {
		private File file;
		private Path to;

		DownloadFile(File file, Path to) {
			this.file = file;
			this.to = to;
		}

		File getFile() {
			return file;
		}

		Path getTo() {
			return to;
		}

	}

	public interface DownloadListener {
		public void getProgress(double status);
	}

	public interface UploadListener {
		public void getProgress(double status);
	}

	private com.google.api.services.drive.Drive service;
	public static String[] scope = {DriveScopes.DRIVE, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA};

	private HashMap<String, File> files = new HashMap<>();

	@Override
	protected void init(Authentication authentication, String appName) {
		service = new com.google.api.services.drive.Drive.Builder(authentication.getHTTP_TRANSPORT(), authentication.getJSON_FACTORY(),
				authentication.getCredential()).setApplicationName(appName).build();
	}

	public List<File> getFilesInRoot(boolean fetch) throws IOException {
		getFiles();
		return getFilesInDirectory("root", fetch);
	}

	public List<File> getFilesInDirectory(String folderID, boolean fetch) throws IOException {
		Children.List request = service.children().list(folderID).setQ("trashed = false");
		List<File> list = new ArrayList<>();
		do {
			try {
				ChildList children = request.execute();

				for (ChildReference child : children.getItems()) {
					list.add(getFile(child.getId(), fetch));
				}
				request.setPageToken(children.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null && request.getPageToken().length() > 0);
		return list;
	}

	public List<File> getFiles() throws IOException {
		this.files.clear();

		List<File> result = new ArrayList<File>();
		Files.List request = service.files().list().setQ("trashed = false");

		do {
			try {
				FileList files = request.execute();

				for (File file : files.getItems()) {
					result.add(file);
					this.files.put(file.getId(), file);
				}

				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null && request.getPageToken().length() > 0);
		return result;
	}

	public List<File> getChildren(File file) {
		List<File> files = new ArrayList<>();
		for (File item : this.files.values()) {
			if (item.getParents().size() > 0) {
				if (file != null) {
					if (item.getParents().get(0).getId().equals(file.getId())) {
						files.add(item);
					}
				} else {
					if (item.getParents().get(0).getIsRoot()) {
						files.add(item);
					}
				}
			}
		}
		return files;
	}

	public File getFile(String id, boolean fetch) throws IOException {
		if (this.files.containsKey(id) && !fetch) {
			return this.files.get(id);
		} else {
			File file = service.files().get(id).execute();
			files.put(file.getId(), file);
			return file;
		}
	}

	public List<File> getTrash() throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = service.files().list().setQ("trashed = true");

		do {
			try {
				FileList files = request.execute();
				for (File file : files.getItems()) {
					result.add(file);
				}

				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null && request.getPageToken().length() > 0);
		return result;
	}

	public void createFolder(String name, String parent) throws IOException {
		File file = new File();
		file.setTitle(name);
		file.setMimeType("application/vnd.google-apps.folder");
		ParentReference parentReference = new ParentReference();
		if (parent != null)
			parentReference.setId(parent);
		else
			parentReference.setId("root");
		ArrayList<ParentReference> list = new ArrayList<>();
		list.add(parentReference);
		file.setParents(list);
		service.files().insert(file).execute();
	}

	public void deleteFile(File file, boolean trash) throws IOException {
		if (trash)
			service.files().trash(file.getId()).execute();
		else
			service.files().delete(file.getId()).execute();
	}

	public void untrash(File file) throws IOException {
		service.files().untrash(file.getId()).execute();
	}

	public void emptyTrash() throws IOException {
		service.files().emptyTrash().execute();
	}

	public void rename(File file, String name) throws IOException {
		file.setTitle(name);
		service.files().patch(file.getId(), file).execute();
	}

	public void copy(File file, File folder, boolean deleteExisting) throws IOException {
		if (deleteExisting)
			for (File child : getChildren(folder)) {
				if (child.getTitle().equals(file.getTitle())) {
					deleteFile(child, false);
				}
			}
		File newFile = new File();
		newFile.setTitle(file.getTitle());
		newFile.setDescription(file.getDescription());
		List<ParentReference> parents = new ArrayList<>();
		if (folder != null) {
			ParentReference parent = new ParentReference();
			parent.setId(folder.getId());
			parents.add(parent);
		} else {
			ParentReference parent = new ParentReference();
			parent.setId("root");
			parents.add(parent);
		}
		newFile.setParents(parents);
		service.files().copy(file.getId(), newFile).execute();
	}

	public void move() {

	}

	public String getFilePath(File file) throws IOException {
		// Root ist Null
		if (file == null) {
			return "/";
		}
		String path = "";
		if (file.getParents().size() != 0) {
			ParentReference parent = file.getParents().get(0);
			File parentFile = getFile(parent.getId(), false);
			path = getFilePath(parentFile) + "/" + file.getTitle() + path;
		}
		return path;
	}

	private int count = 0;

	public Path downloadFolder(File folder, Path to, DownloadListener listener) throws IOException {
		Path path = to.resolve(folder.getTitle());
		List<DownloadFile> files = getFolderFiles(folder, path);
		count = 0;

		for (DownloadFile file : files) {
			Path fullPath = file.getTo();
			if (java.nio.file.Files.notExists(fullPath)) {
				java.nio.file.Files.createDirectories(fullPath.getParent());
				java.nio.file.Files.createFile(fullPath);
			}
			OutputStream ost = new FileOutputStream(fullPath.toFile());
			downloadFile(file.getFile(), ost, (downloader) -> {
				switch (downloader.getDownloadState()) {
					case MEDIA_COMPLETE:
						count++;
						System.out.println(file.getFile().getTitle() + ": Download is complete!");
						listener.getProgress(count / (float) files.size());
						ost.close();
						break;
					default:
						break;
				}
			});
		}
		return path;
	}

	private List<DownloadFile> getFolderFiles(File folder, Path to) throws IOException {
		List<DownloadFile> files = new ArrayList<>();
		for (File child : getChildren(folder)) {
			if (isFolder(child)) {
				files.addAll(getFolderFiles(child, to.resolve(child.getTitle())));
			} else {
				// Keine Google Datentypen wie Tabellen
				if (!child.getMimeType().startsWith("application/vnd.google-apps")) {
					Path fullPath = to.resolve(child.getTitle());
					files.add(new DownloadFile(child, fullPath));
				}
			}
		}
		return files;
	}

	public Path downloadFile(File file, Path to, DownloadListener listener) throws IOException {
		Path fullPath = to.resolve(file.getTitle());
		if (java.nio.file.Files.notExists(fullPath)) {
			java.nio.file.Files.createDirectories(fullPath.getParent());
			java.nio.file.Files.createFile(fullPath);
		}
		OutputStream ost = new FileOutputStream(fullPath.toFile());
		downloadFile(file, ost, (downloader) -> {
			switch (downloader.getDownloadState()) {
				case MEDIA_IN_PROGRESS:
					listener.getProgress(downloader.getProgress());
					break;
				case MEDIA_COMPLETE:
					System.out.println(file.getTitle() + ": Download is complete!");
					listener.getProgress(1);
					ost.close();
					break;
				case NOT_STARTED:
					listener.getProgress(-1);
					break;
			}
		});
		return fullPath;
	}

	private void downloadFile(File file, OutputStream out, MediaHttpDownloaderProgressListener listener) throws IOException {
		Files.Get request = service.files().get(file.getId());
		request.getMediaHttpDownloader().setProgressListener(listener);
		request.executeMediaAndDownloadTo(out);
	}

	public File updateFile(File file, Path path, UploadListener listener) throws IOException {
		FileContent mediaContent = new FileContent(file.getMimeType(), path.toFile());

		Update request = service.files().update(file.getId(), file, mediaContent);
		request.getMediaHttpUploader().setProgressListener((uploader) -> {
			switch (uploader.getUploadState()) {
				case MEDIA_COMPLETE:
					listener.getProgress(1);
					System.out.println(file.getTitle() + ": Update is complete!");
					break;
				case MEDIA_IN_PROGRESS:
					listener.getProgress(uploader.getProgress());
					break;
				case NOT_STARTED:
					listener.getProgress(-1);
					break;
				default:
					break;
			}
		});
		File f = request.execute();
		return f;
	}

	public static boolean isFolder(File file) {
		return file == null || file.getMimeType().equals("application/vnd.google-apps.folder");
	}

	public File uploadFile(Path path, File destinationFolder, UploadListener uploadListener, String description) throws IOException {
		String mimeType = getMimeType(path);

		File body = new File();
		body.setTitle(path.getFileName().toString());
		body.setDescription(description);
		body.setMimeType(mimeType);

		if (destinationFolder != null) {
			String parentId = destinationFolder.getId();
			if (parentId != null && parentId.length() > 0) {
				body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
			}
		}
		FileContent mediaContent = new FileContent(mimeType, path.toFile());

		Insert request = service.files().insert(body, mediaContent);
		request.getMediaHttpUploader().setProgressListener((uploader) ->
		{
			switch (uploader.getUploadState()) {
				case MEDIA_COMPLETE:
					uploadListener.getProgress(1);
					System.out.println(path.getFileName() + ": Update is complete!");
					break;
				case MEDIA_IN_PROGRESS:
					uploadListener.getProgress(uploader.getProgress());
					break;
				case NOT_STARTED:
					uploadListener.getProgress(-1);
					break;
				default:
					break;
			}
		});
		File file = request.execute();
		return file;
	}

	public static String getMimeType(Path path) {
		switch (FileUtils.getFileExtention(path)) {
			case "xls":
				return "application/vnd.ms-excel";
			case "xlsx":
				return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			case "xml":
				return "text/xml";
			case "ods":
				return "application/vnd.oasis.opendocument.spreadsheet";
			case "csv":
				return "text/plain";
			case "tmpl":
				return "text/plain";
			case "pdf":
				return "application/pdf";
			case "php":
				return "application/x-httpd-php";
			case "txt":
				return "text/plain";
			case "doc":
				return "application/msword";
			case "docx":
				return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			case "odt":
				return "application/vnd.oasis.opendocument.text";
			case "ppt":
				return "application/mspowerpoint";
			case "pptx":
				return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
			case "js":
				return "text/js";
			case "swf":
				return "application/x-shockwave-flash";
			case "mp3":
				return "audio/mpeg";
			case "zip":
				return "application/zip";
			case "html":
				return "text/html";
			case "htm":
				return "text/html";
			case "jar":
				return "application/java-archive";
			default:
				System.out.println("Search for mime type: " + FileUtils.getFileExtention(path));
				MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
				return mimeTypesMap.getContentType(path.toFile().getName());

		}
	}
}