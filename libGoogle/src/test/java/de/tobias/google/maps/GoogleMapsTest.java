package de.tobias.google.maps;

import java.io.IOException;

public class GoogleMapsTest {
	public static void main(String[] args) throws IOException {
		System.out.println(GoogleMaps.latitude("Bestensee", "AIzaSyBDtQUby_ol9xLYKnwnLMvmMP-MC3Ui2dM"));
		System.out.println(GoogleMaps.longitude("Bestensee", "AIzaSyBDtQUby_ol9xLYKnwnLMvmMP-MC3Ui2dM"));
	}
}
