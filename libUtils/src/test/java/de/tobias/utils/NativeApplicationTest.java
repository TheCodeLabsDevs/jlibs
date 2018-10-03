package de.tobias.utils;

import de.tobias.utils.application.system.NativeApplication;
import de.tobias.utils.application.system.NativeFeature;

public class NativeApplicationTest {
	public static void main(String[] args) {
		System.out.println(NativeApplication.sharedInstance().isFeatureSupported(NativeFeature.DOCK_HIDDEN));
	}
}
