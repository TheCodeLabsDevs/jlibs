package de.tobias.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import de.tobias.utils.application.ApplicationUtils;
import de.tobias.utils.application.container.PathType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Authentication {

	private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	private final JsonFactory JSON_FACTORY = new JacksonFactory();

	private Credential credential;

	public static Authentication authenticate(String path, String username, URL clientJson, String[]... scopes) throws IOException {
		Authentication authentication = new Authentication();

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(authentication.JSON_FACTORY,
				new InputStreamReader(clientJson.openStream()));

		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=youtube"
					+ "into youtube-cmdline-channelbulletin-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		List<String> s = new ArrayList<>();
		for (String[] scope : scopes) {
			Collections.addAll(s, scope);
		}

		FileDataStoreFactory credentialStore = new FileDataStoreFactory(ApplicationUtils.getApplication().getPath(PathType.STORE, path)
				.toFile());
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(authentication.HTTP_TRANSPORT,
				authentication.JSON_FACTORY, clientSecrets, s).setDataStoreFactory(credentialStore).build();
		LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();
		authentication.credential = new AuthorizationCodeInstalledApp(flow, localReceiver).authorize(username);

		return authentication;
	}

	public HttpTransport getHTTP_TRANSPORT() {
		return HTTP_TRANSPORT;
	}

	public JsonFactory getJSON_FACTORY() {
		return JSON_FACTORY;
	}

	public Credential getCredential() {
		return credential;
	}
}
