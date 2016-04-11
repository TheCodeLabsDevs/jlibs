package de.tobias.utils.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class HttpUtils {

	public static String sendPostRequest(String addr, HashMap<String, String> args) throws IOException {
		String body = "";
		int i = 0;
		for (String key : args.keySet()) {
			body += key + "=" + URLEncoder.encode(args.get(key), "UTF-8");
			if (i + 1 < args.size()) {
				body += "&";
			}
			i++;
		}

		URL url = new URL(addr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(body);
		writer.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder builder = new StringBuilder();
		
		for (String line; (line = reader.readLine()) != null;) {
			builder.append(line);
			builder.append("\n");
		}

		writer.close();
		reader.close();
		return builder.toString();
	}
}
