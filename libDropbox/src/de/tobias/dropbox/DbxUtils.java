package de.tobias.dropbox;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DbxUtils {

	/**
	 * Erstellt ein neuen Dropbox Zugang für java.nio. Env muss folgendes enthalten: appInfo und asseccToken. AppInfo ist der Name und
	 * accessToken der Token von Dbx.
	 * 
	 * @param env
	 *            Env
	 * @return
	 * @throws DbxException
	 *             Dbx Fehler
	 * @throws IOException
	 *             IO Fehler
	 */
	public static FileSystem createAttachPoint(Map<String, String> env) throws DbxException, IOException {
		DbxRequestConfig config = new DbxRequestConfig(env.get("appInfo"));
		DbxClientV2 client = new DbxClientV2(config, env.get("accessToken"));

		String name = client.users().getCurrentAccount().getName().getDisplayName().replace(" ", "");
		URI host = URI.create("dropbox://" + name + "/");

		HashMap<String, Object> userInfo = new HashMap<>();
		userInfo.put("client", client);

		return FileSystems.newFileSystem(host, userInfo);
	}

	// TODO Rewrite
//	public static String shareFile(DbxPath path) throws DbxException {
//		return path.getFileSystem().getDbxClient().createShareableUrl(path.toString());
//	}
}
