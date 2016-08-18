package de.tobias.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertStatement<T> implements StatementBase {

	private T data;

	private Nameable type;
	private Class<?> keyClazz;

	private Connection conn;

	public InsertStatement(Connection conn, T data) {
		this.data = data;
		keyClazz = data.getClass();

		this.conn = conn;

		if (keyClazz.isAnnotationPresent(Nameable.class)) {
			type = (keyClazz.getAnnotation(Nameable.class));
		}
	}

	@Override
	public boolean execute() throws SQLException {
		Statement stmt = conn.createStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("INSERT INTO ");
		// Tables
		builder.append(this.type.value());

		builder.append(" VALUES (");

		Field[] fields = keyClazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (field.isAnnotationPresent(Ignore.class)) {
				builder.append("''");
			} else if (field.isAnnotationPresent(Nameable.class)) {
				try {
					Object data = field.get(this.data);

					builder.append("'");
					builder.append(data);
					builder.append("'");
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			if (i + 1 < fields.length)
				builder.append(", ");
		}
		builder.append(")");

		// Values

		builder.append(";");

		stmt.close();

		return stmt.execute(builder.toString());
	}
}
