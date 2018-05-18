package de.tobias.google;


public abstract class Service {

	protected abstract void init(Authentication authentication, String appName);

	public static <T extends Service> T create(Class<T> service, Authentication authentication, String appName) throws InstantiationException, IllegalAccessException {
		T t = service.newInstance();
		t.init(authentication, appName);
		return t;
	}
}
