package de.tobias.dropbox.test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import de.tobias.dropbox.DbxUtils;

public class DbxTest {

	public static void main(String[] args) throws IOException, DbxException {
		DbxRequestConfig requestConfig = new DbxRequestConfig("PlayWall");

//		DbxAppInfo appInfo = DbxAppInfo.Reader.readFromFile("api.app");
//		DbxWebAuth auth = new DbxWebAuth(requestConfig, appInfo);
//
//		DbxWebAuth.Request authRequest = DbxWebAuth.newRequestBuilder().withNoRedirect().build();
//		String authorizeUrl = auth.authorize(authRequest);
//		System.out.println("1. Go to " + authorizeUrl);
//		System.out.println("2. Click \"Allow\" (you might have to log in first).");
//		System.out.println("3. Copy the authorization code.");
//		System.out.print("Enter the authorization code here: ");
//
//		String code = System.console().readLine();
//		if (code != null) {
//			code = code.trim();
//			DbxAuthFinish authFinish = webAuth.finishFromCode(code);
//			DbxClientV2 client = new DbxClientV2(requestConfig, authFinish.getAccessToken());
//		}

		DbxClientV2 client = new DbxClientV2(requestConfig, "7HkiQL88B1UAAAAAAAAB6kndV7WwXtRyYP_SEKHNcLV10SEqYJBsMeAQhsFCHCP3");

		FullAccount account = client.users().getCurrentAccount();
		System.out.println(account.getName().getDisplayName());
		
		Map<String, String> env = new HashMap<>();
		env.put("appInfo", "PlayWall");
		env.put("accessToken", "7HkiQL88B1UAAAAAAAAB6kndV7WwXtRyYP_SEKHNcLV10SEqYJBsMeAQhsFCHCP3");
		FileSystem system = DbxUtils.createAttachPoint(env);
		
		Path org = system.getPath("/probeklausur englisch.pdf");
		Path copy = system.getPath("/Copy.pdf");
		
//		System.out.println(org.toUri());
//		System.out.println(copy.toUri());
		
		Files.copy(org, copy);

		ListFolderResult result = client.files().listFolder("");
		while (true) {
			for (Metadata metadata : result.getEntries()) {
				System.out.println(metadata.getPathLower());
			}

			if (!result.getHasMore()) {
				break;
			}

			result = client.files().listFolderContinue(result.getCursor());
		}
	}
}
