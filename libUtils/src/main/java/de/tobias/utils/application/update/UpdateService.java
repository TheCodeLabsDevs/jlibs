package de.tobias.utils.application.update;

import de.tobias.utils.application.App;

public interface UpdateService {

	void update(App app, long oldVersion, long newVersion);
}
