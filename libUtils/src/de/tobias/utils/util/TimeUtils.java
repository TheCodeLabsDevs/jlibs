package de.tobias.utils.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import javafx.util.Duration;

public class TimeUtils {

	private static DateFormat dfmHours;
	private static DateFormat dfmShort;
	private static DateFormat dfmLong;
	private static DateFormat dfmLongLong;
	private static DateFormat dfmDay;

	static {
		dfmHours = new SimpleDateFormat("HH");
		dfmShort = new SimpleDateFormat("HH:mm");
		dfmLong = new SimpleDateFormat("dd.MM. HH:mm");
		dfmDay = new SimpleDateFormat("EEE dd.MM.");
		dfmLongLong = new SimpleDateFormat("dd.MM. HH:mm:ss");
	}

	public static DateFormat getDfmDay() {
		return dfmDay;
	}

	public static DateFormat getDfmHours() {
		return dfmHours;
	}

	public static DateFormat getDfmLong() {
		return dfmLong;
	}

	public static DateFormat getDfmShort() {
		return dfmShort;
	}

	public static DateFormat getDfmLongLong() {
		return dfmLongLong;
	}

	public static double calculateNeededTime(long total, long completed, long pastTime) {
		double timePerByte = (double) pastTime / (double) completed;
		long future = total - completed;
		return future * timePerByte;
	}

	public static Optional<Duration> parse(String input) {
		if (!input.endsWith("s")) {
			input += "s";
		}
		input = input.replace(" ", "").replace(",", ".");
		if (input.matches("\\d+(\\.\\d+)s") || input.matches("\\d+s")) {
			return Optional.of(Duration.valueOf(input));
		}
		return Optional.empty();
	}

	public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
		if ((birthDate != null) && (currentDate != null)) {
			return Period.between(birthDate, currentDate).getYears();
		} else {
			return 0;
		}
	}
}
