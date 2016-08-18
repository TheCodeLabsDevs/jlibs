package de.tobias.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SelectStatement<T> implements StatementBase {

	private List<T> result;

	private Nameable type;
	private Class<T> keyClazz;

	private List<WhereCondition> conditions;
	private Connection conn;

	public SelectStatement(Connection conn, Class<T> clazz) {
		this.result = new ArrayList<>();
		this.conditions = new ArrayList<>();

		keyClazz = clazz;

		this.conn = conn;

		if (clazz.isAnnotationPresent(Nameable.class)) {
			type = (clazz.getAnnotation(Nameable.class));
		}
	}

	public void addCondition(WhereCondition condition) {
		this.conditions.add(condition);
	}

	@Override
	public boolean execute() throws SQLException {
		Statement stmt = conn.createStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append("*");
		builder.append(" FROM ");

		// Tables
		builder.append(this.type.value());

		// Where
		if (!conditions.isEmpty()) {
			builder.append(" WHERE ");
			builder.append(conditions.get(0).toString());
			for (int i = 1; i < conditions.size(); i++) {
				builder.append(" AND " + conditions.get(i).toString());
			}
		}

		builder.append(";");

		boolean success = stmt.execute(builder.toString());

		ResultSet set = stmt.getResultSet();
		while (set.next()) {
			try {
				T t = keyClazz.newInstance();
				for (Field field : keyClazz.getDeclaredFields()) {
					if (field.isAnnotationPresent(Nameable.class)) {
						String name = field.getAnnotation(Nameable.class).value();
						field.setAccessible(true);
						field.set(t, set.getObject(name));
					}
				}
				result.add(t);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		stmt.close();
		set.close();
		
		return success;
	}

	public List<T> getResult() {
		return result;
	}
}
