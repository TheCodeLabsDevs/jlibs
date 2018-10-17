package de.thecodelabs.utils.application.update;

import de.thecodelabs.utils.application.App;

public interface UpdateService {

	void update(App app, long oldVersion, long newVersion);
}
