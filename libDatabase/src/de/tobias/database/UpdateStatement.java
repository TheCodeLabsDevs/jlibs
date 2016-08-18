package de.tobias.database;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateStatement<T> implements StatementBase {

	private T data;

	private Nameable type;
	private Class<?> keyClazz;

	private Connection conn;

	public UpdateStatement(Connection conn, T data) {
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

		builder.append("UPDATE ");
		// Tables
		builder.append(this.type.value());

		builder.append(" SET ");

		Field keyField = null;

		Field[] fields = keyClazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			// Find Key Field
			if (field.isAnnotationPresent(Key.class) && field.isAnnotationPresent(Nameable.class)) {
				keyField = field;
			}

			// set Values from fields
			if (field.isAnnotationPresent(Nameable.class) && !field.isAnnotationPresent(Ignore.class) && !field.isAnnotationPresent(Key.class)) {
				try {
					String name = field.getAnnotation(Nameable.class).value();

					// Key
					builder.append(name);
					builder.append("=");

					Object data = field.get(this.data);

					// Data
					builder.append("'");
					builder.append(data);
					builder.append("'");
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

				if (i + 1 < fields.length)
					builder.append(", ");
			}
		}
		builder.append(" ");

		// Where
		if (keyField == null) {
			return false;
		}
		try {
			String keyName = keyField.getAnnotation(Nameable.class).value();
			Object obj = keyField.get(data);

			builder.append("WHERE ");
			builder.append(keyName);
			builder.append("='");
			builder.append(obj);
			builder.append("'");
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}

		// Values

		builder.append(";");
		System.out.println(builder);

		boolean execute = stmt.execute(builder.toString());

		stmt.close();
		return execute;
	}
}