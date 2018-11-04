package de.tobias.logger;

import de.thecodelabs.logger.FileOutputOption;
import de.thecodelabs.logger.LogLevel;
import de.thecodelabs.logger.LogLevelFilter;
import de.thecodelabs.logger.Logger;

import java.nio.file.Paths;

public class LoggerTest {

	public static void main(String[] args) {
		Logger.init(Paths.get("log"));
		Logger.setFileOutput(FileOutputOption.SPLITTED);
		Logger.setLevelFilter(LogLevelFilter.TRACE);
		Logger.log(LogLevel.TRACE, "Test");
		Logger.log(LogLevel.ERROR, "Test");
		Logger.log(LogLevel.TRACE, "Test");
		Logger.log(LogLevel.ERROR, "Test");

		Logger.appInfo("Sample Project", "1.0.0", "1", "01.01.1970");

		Logger.deleteLogfile();
	}
}
