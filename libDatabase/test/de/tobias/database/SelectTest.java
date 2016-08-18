package de.tobias.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SelectTest {

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
	public void selectTest() throws Exception {
		SelectStatement<TestClassA> select = new SelectStatement<>(conn, TestClassA.class);
		select.execute();
		List<TestClassA> result = select.getResult();
		Assert.assertEquals(2, result.size());
		System.out.println("SELECT Result: " + result);
	}

	@Test
	public void whereTest() throws Exception {
		SelectStatement<TestClassA> select = new SelectStatement<>(conn, TestClassA.class);
		select.addCondition(new WhereCondition("fieldA", "Hallo"));

		select.execute();
		List<TestClassA> result = select.getResult();
		Assert.assertEquals(1, result.size());
		System.out.println("SELECT Result: " + result);
	}
}
