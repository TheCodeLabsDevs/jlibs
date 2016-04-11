package de.tobias.utils.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import de.tobias.utils.application.ApplicationUtils;

public class ConsoleUtils {

	public static enum Color {
		RESET("\u001B[0m"),
		YELLOW("\u001B[33m"),
		RED("\u001B[31m"),
		BLUE("\u001B[34m"),
		GREEN("\u001B[32m"),
		BLACK("\u001B[30m"),
		PURPLE("\u001B[35m"),
		CYAN("\u001B[36m"),
		WHITE("\u001B[37m");

		private String code;

		private Color(String code) {
			this.code = code;
		}
	}

	public static void setConsoleColor(Color color) {
		System.out.print(color.code);
	}

	public static PrintStream convertStream(PrintStream stream, String prefix) {
		return new ConsoleStream(stream, prefix);
	}

	@Deprecated
	public static PrintStream streamToFile(File file, String prefix) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return new ConsoleFileStream(file.toPath(), prefix);
	}

	public static PrintStream streamToFile(Path file) throws IOException {
		return streamToFile(file, "");
	}

	public static PrintStream streamToFile(Path file, String prefix) throws IOException {
		if (Files.notExists(file)) {
			Files.createDirectories(file.getParent());
			Files.createFile(file);
		}
		return new ConsoleFileStream(file, prefix);
	}

	private static class ConsoleStream extends PrintStream {

		private SimpleDateFormat format = new SimpleDateFormat("dd-MM-YY HH:mm:ss");
		private String prefix;

		public ConsoleStream(OutputStream out, String prefix) {
			super(out);
			this.prefix = prefix;
		}

		@Override
		public void print(String obj) {
			StackTraceElement element = Thread.currentThread().getStackTrace()[3];
			super.print(prefix + format.format(System.currentTimeMillis()) + " [" + element.getClassName() + "." + element.getMethodName() + ":"
					+ element.getLineNumber() + "]: " + obj);
		}

		@Override
		public void print(int obj) {
			StackTraceElement element = Thread.currentThread().getStackTrace()[3];
			super.print(prefix + format.format(System.currentTimeMillis()) + " [" + element.getClassName() + "." + element.getMethodName() + ":"
					+ element.getLineNumber() + "]: " + obj);
		}

		@Override
		public void print(float obj) {
			StackTraceElement element = Thread.currentThread().getStackTrace()[3];
			super.print(prefix + format.format(System.currentTimeMillis()) + " [" + element.getClassName() + "." + element.getMethodName() + ":"
					+ element.getLineNumber() + "]: " + obj);
		}
	}

	private static class ConsoleFileStream extends PrintStream {

		private SimpleDateFormat format = new SimpleDateFormat("dd-MM-YY HH:mm:ss");
		private String prefix;

		public ConsoleFileStream(Path file, String prefix) throws FileNotFoundException {
			super(new FileOutputStream(file.toFile(), true));
			this.prefix = prefix;
		}

		@Override
		public void print(String obj) {
			if (ApplicationUtils.getApplication().isDebug()) {
				StackTraceElement element = Thread.currentThread().getStackTrace()[3];
				super.print(prefix + format.format(System.currentTimeMillis()) + " [" + element.getClassName() + "." + element.getMethodName()
						+ ":" + element.getLineNumber() + "]: " + obj);
			} else {
				super.print(prefix + format.format(System.currentTimeMillis()) + ": " + obj);
			}
		}

		@Override
		public void print(int obj) {
			if (ApplicationUtils.getApplication().isDebug()) {
				StackTraceElement element = Thread.currentThread().getStackTrace()[3];
				super.print(prefix + format.format(System.currentTimeMillis()) + " [" + element.getClassName() + "." + element.getMethodName()
						+ ":" + element.getLineNumber() + "]: " + obj);
			} else {
				super.print(prefix + format.format(System.currentTimeMillis()) + ": " + obj);
			}
		}

		@Override
		public void print(float obj) {
			if (ApplicationUtils.getApplication().isDebug()) {
				StackTraceElement element = Thread.currentThread().getStackTrace()[3];
				super.print(prefix + format.format(System.currentTimeMillis()) + " [" + element.getClassName() + "." + element.getMethodName()
						+ ":" + element.getLineNumber() + "]: " + obj);
			} else {
				super.print(prefix + format.format(System.currentTimeMillis()) + ": " + obj);
			}
		}
	}
}
