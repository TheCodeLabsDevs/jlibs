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
		String s = formatString(b);
		super.print(s);
	}

	@Override
	public void print(char c) {
		String s = formatString(c);
		super.print(s);
	}

	@Override
	public void print(double d) {
		String s = formatString(d);
		super.print(s);
	}

	@Override
	public void print(float f) {
		String s = formatString(f);
		super.print(s);
	}

	@Override
	public void print(int i) {
		String s = formatString(i);
		super.print(s);
	}

	@Override
	public void print(long l) {
		String s = formatString(l);
		super.print(s);
	}

	@Override
	public void print(Object obj) {
		obj = formatString(obj);
		super.print(obj);
	}

	@Override
	public void print(String s) {
		s = formatString(s);
		super.print(s);
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
	private String formatString(Object obj) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[4];
		if (!element.getClassName().equals(Logger.class.getName())) {
			LogMessage logMessage = new LogMessage(standardLogLevel, obj.toString(), element.getClassName());
			return logMessage.buildString(loggerConfig);
		}
		return obj.toString();
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
