package de.tobias.database;

public class WhereCondition {

	private final String key;
	private final Object data;

	public WhereCondition(String key, Object data) {
		this.key = key;
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		return "\"" + key + "\"=\"" + data.toString() + "\"";
	}
}
