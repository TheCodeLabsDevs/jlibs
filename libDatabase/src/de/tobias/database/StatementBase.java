package de.tobias.database;

import java.sql.SQLException;

public interface StatementBase {

	public boolean execute() throws SQLException;
}
