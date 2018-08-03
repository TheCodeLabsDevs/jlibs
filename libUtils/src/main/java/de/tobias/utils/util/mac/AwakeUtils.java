package de.tobias.utils.util.mac;

public class AwakeUtils {

	public static void load(String filename) {
		System.load(filename);
		INSTANCE = new AwakeUtils();
	}

	private static AwakeUtils INSTANCE;

	private AwakeUtils() {}

	public native void lock();

	public native void unlock();

	public static AwakeUtils getInstance() {
		return INSTANCE;
	}
}
