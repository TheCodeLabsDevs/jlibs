package de.tobias.google.maps;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class GoogleMaps {

	private static final String url = "http://maps.googleapis.com/maps/api/geocode/json?address=";

	private static HashMap<String, Double> latitudes = new HashMap<>();
	private static HashMap<String, Double> longitudes = new HashMap<>();

	public static double latitude(String place) throws IOException {
		return latitudes.containsKey(place) ? latitudes.get(place) : queryPlace(place);
	}

	public static double longitude(String place) throws IOException {
		return longitudes.containsKey(place) ? longitudes.get(place) : queryPlace(place);
	}

	private static double queryPlace(String place) throws IOException {
		URL url = new URL(GoogleMaps.url + place);
		try {
			JSONObject object = (JSONObject) JSONValue.parse(url.openStream());
			JSONObject data = (JSONObject) ((JSONObject) ((JSONObject) ((JSONArray) object.get("results")).get(0)).get("geometry"))
					.get("location");

			latitudes.put(place, (Double) data.get("lat"));
			longitudes.put(place, (Double) data.get("lng"));

			return latitudes.get(place);
		} catch (Exception e) {
			return Double.NaN;
		}
	}
}
