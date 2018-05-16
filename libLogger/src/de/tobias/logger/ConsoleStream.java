package de.tobias.logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConsoleStream extends PrintStream {

	private final PrintStream source;
	private boolean fileOutput;

	private final LogLevel standardLogLevel;
	private final LoggerConfig loggerConfig;

	public ConsoleStream(Path path, PrintStream source, LogLevel standardLogLevel, LoggerConfig loggerConfig) throws IOException {
		super(Files.newOutputStream(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE));

		this.source = source;
		this.fileOutput = false;

		this.standardLogLevel = standardLogLevel;
		this.loggerConfig = loggerConfig;
	}

	@Override
	public void print(boolean b) {
		if (checkAndUseLogger(b)) {
			super.print(b);
		}
	}

	@Override
	public void print(char c) {
		if (checkAndUseLogger(c)) {
			super.print(c);
		}
	}

	@Override
	public void print(double d) {
		if (checkAndUseLogger(d)) {
			super.print(d);
		}
	}

	@Override
	public void print(float f) {
		if (checkAndUseLogger(f)) {
			super.print(f);
		}
	}

	@Override
	public void print(int i) {
		if (checkAndUseLogger(i)) {
			super.print(i);
		}
	}

	@Override
	public void print(long l) {
		if (checkAndUseLogger(l)) {
			super.print(l);
		}
	}

	@Override
	public void print(Object obj) {
		if (checkAndUseLogger(obj)) {
			super.print(obj);
		}
	}

	@Override
	public void print(String s) {
		if (checkAndUseLogger(s)) {
			super.print(s);
		}
	}

	private boolean skipColor = false;

	// Handle print --> file or console
	@Override
	public void write(int b) {
		if (fileOutput) {
			if (b == '\u001B') {
				skipColor = true;
			}

			if (!skipColor) {
				super.write(b);
			}

			if (b == 'm') {
				skipColor = false;
			}
		}
		source.write(b);
	}

	// TODO Improve code performance
	@Override
	public void write(@Nonnull byte[] buf, int off, int len) {
		for (int i = off; i < len; i++) {
			write(buf[i]);
		}
	}

	// Add standard format, if user uses System.out / System.err
	private boolean checkAndUseLogger(Object obj) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[4];
		if (!element.getClassName().equals(Logger.class.getName())) {
			Logger.log(standardLogLevel, obj);
			return false;
		}
		return true;
	}


	public void setFileOutput(boolean fileOutput) {
		this.fileOutput = fileOutput;
	}

	public PrintStream getSource() {
		return source;
	}

	public static int find(byte[] array, byte value, int start) {
		for (int i = start; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
}
