package de.tobias.utils.application.remote;

import de.tobias.utils.application.App;
import de.tobias.utils.util.StringUtils;

import java.net.URL;

public class RemoteResourceHandler {

	private App app;

	public RemoteResourceHandler(App app) {
		this.app = app;
	}

	public RemoteResource get(RemoteResourceType type, String... childPath) {
		final String child = StringUtils.build(childPath, "/");
		String urlString = String.format("%s/%s", app.getInfo().getServerBaseURL(), child);
		try {
			URL url = new URL(urlString);
			return new RemoteResource(url.openConnection());
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}
}
