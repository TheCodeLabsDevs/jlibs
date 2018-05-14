package de.tobias.logger;

import java.nio.file.Paths;

public class LoggerTest {

	public static void main(String[] args) {
		Logger.init(Paths.get("log"));
		Logger.setFileOutput(FileOutputOption.DISABLE);
		Logger.log(LogLevel.INFO, "Test");
	}
}
