package de.tobias.utils;

import de.tobias.logger.LogLevelFilter;
import de.tobias.logger.Logger;
import de.tobias.utils.logger.LoggerBridge;

import java.nio.file.Paths;

public class LoggerTest {
    public static void main(String[] args) {
        Logger.init(Paths.get("./"));
        Logger.setLevelFilter(LogLevelFilter.DEBUG);

        LoggerBridge.trace("Test");
        LoggerBridge.debug("Test");
        LoggerBridge.info("Test");
        LoggerBridge.warning("Test");
        LoggerBridge.error("Test");
        LoggerBridge.fatal("Test");
    }
}
