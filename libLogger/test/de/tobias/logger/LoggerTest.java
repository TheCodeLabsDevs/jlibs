package de.tobias.logger;

import java.nio.file.Paths;

public class LoggerTest {

	public static void main(String[] args) {
		Logger.init(Paths.get("log"));
		Logger.setFileOutput(FileOutputOption.DISABLED);
		Logger.setLevelFilter(LogLevelFilter.TRACE);
		Logger.log(LogLevel.TRACE, "Test");
		System.out.println("binopmü");
		System.out.print("ibnomp");
	}
}
