package de.tobias.utils.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLUtils {

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection(String host, String db, int port, String username, String password) throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, username, password);
	}
}
