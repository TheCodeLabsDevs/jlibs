package de.tobias.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class InsertTest {

	static Connection conn;

	@BeforeClass
	public static void beforeClass() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:sample.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void afterClass() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void insertTest() throws Exception {
		TestClassA data = new TestClassA();
		data.fieldA = "Bla";
		data.fieldB = "Keks";
		InsertStatement<TestClassA> insert = new InsertStatement<TestClassA>(conn, data);
		insert.execute();
	}
}
