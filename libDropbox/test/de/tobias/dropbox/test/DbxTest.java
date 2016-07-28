package de.tobias.dropbox.test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.dropbox.core.DbxException;

import de.tobias.dropbox.DbxUtils;

public class DbxTest {

	public static void main(String[] args) throws IOException, DbxException {
		// DbxAppInfo appInfo = DbxAppInfo.Reader.readFromFile("api.app");
		// DbxWebAuth auth = new DbxWebAuth(requestConfig, appInfo);
		//
		// DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();
		// String authorizeUrl = auth.authorize(authRequest);
		// System.out.println("1. Go to " + authorizeUrl);
		// System.out.println("2. Click \"Allow\" (you might have to log in first).");
		// System.out.println("3. Copy the authorization code.");
		// System.out.print("Enter the authorization code here: ");
		//
		// String code = System.console().readLine();
		// if (code != null) {
		// code = code.trim();
		// DbxAuthFinish authFinish = webAuth.finishFromCode(code);
		// DbxClientV2 client = new DbxClientV2(requestConfig, authFinish.getAccessToken());
		// }

		Map<String, String> env = new HashMap<>();
		env.put("appInfo", "PlayWall");
		env.put("accessToken", "7HkiQL88B1UAAAAAAAAB6kndV7WwXtRyYP_SEKHNcLV10SEqYJBsMeAQhsFCHCP3");
		DbxUtils.createAttachPoint(env);

		Path path = Paths.get(URI.create("dropbox:///"));

		// Files.createDirectories(Paths.get(URI.create("dropbox:/Cache")));
		Files.copy(Paths.get("/Users/tobias/Downloads/ok.mp3"), Paths.get(URI.create("dropbox:/Cache/ok.mp3")));

		Files.newDirectoryStream(path).forEach(System.out::println);
	}
}
