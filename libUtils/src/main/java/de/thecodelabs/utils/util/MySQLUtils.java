package de.thecodelabs.utils.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLUtils {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection(String host, String db, int port, String username, String password) throws SQLException {
		Properties properties = new Properties();
		properties.put("user", username);
		properties.put("password", password);
		properties.put("autoReconnect", "true");
		return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, properties);
	}
}
