package de.tobias.logger;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConsoleStream extends PrintStream {

	private boolean fileOutput;
	private final PrintStream source;
	private final LogLevel standardLogLevel;

	public ConsoleStream(Path path, PrintStream source, LogLevel standardLogLevel) throws IOException {
		super(Files.newOutputStream(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE));
		this.source = source;
		this.standardLogLevel = standardLogLevel;
		this.fileOutput = false;
	}

	@Override
	public void print(boolean b) {
		String s = printToLogger(b);
		super.print(s);
	}

	@Override
	public void print(char c) {
		String s = printToLogger(c);
		super.print(s);
	}

	@Override
	public void print(double d) {
		String s = printToLogger(d);
		super.print(s);
	}

	@Override
	public void print(float f) {
		super.print(f);
	}

	@Override
	public void print(int i) {
		String s = printToLogger(i);
		super.print(s);
	}

	@Override
	public void print(long l) {
		String s = printToLogger(l);
		super.print(s);
	}

	@Override
	public void print(Object obj) {
		obj = printToLogger(obj);
		super.print(obj);
	}

	@Override
	public void print(String s) {
		s = printToLogger(s);
		super.print(s);
	}

	@Override
	public void write(int b) {
		if (fileOutput)
			super.write(b);
		source.write(b);
	}

	@Override
	public void write(byte[] buf, int off, int len) {
		if (fileOutput)
			super.write(buf, off, len);
		source.write(buf, off, len);
	}

	private String printToLogger(Object obj) {
		StackTraceElement element = Thread.currentThread().getStackTrace()[4];
		if (!element.getClassName().equals("de.tobias.logger.Logger")) {
			return Logger.buildString(standardLogLevel, obj.toString(), element.getClassName());
		}
		return obj.toString();
	}
	
	
	public void setFileOutput(boolean fileOutput) {
		this.fileOutput = fileOutput;
	}
}
