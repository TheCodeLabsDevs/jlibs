package de.thecodelabs.utils.util;

import javafx.util.Duration;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Optional;

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

	public static DateFormat getFormatterDay() {
		return dfmDay;
	}

	public static DateFormat getFormatterHours() {
		return dfmHours;
	}

	public static DateFormat getFormatterLong() {
		return dfmLong;
	}

	public static DateFormat getFormatterShort() {
		return dfmShort;
	}

	public static DateFormat getFormatterLongLong() {
		return dfmLongLong;
	}

	public static double calculateEstimatedTime(long total, long completed, long pastTime) {
		double timePerByte = (double) pastTime / (double) completed;
		long future = total - completed;
		return future * timePerByte;
	}

	public static Optional<Duration> parseDuration(String input) {
		if (!input.endsWith("s")) {
			input += "s";
		}
		input = input.replace(" ", "").replace(",", ".");
		if (input.matches("\\d+(\\.\\d+)s") || input.matches("\\d+s")) {
			return Optional.of(Duration.valueOf(input));
		}
		return Optional.empty();
	}

	public static int calculateYearDistance(LocalDate birthDate, LocalDate currentDate) {
		if ((birthDate != null) && (currentDate != null)) {
			return Period.between(birthDate, currentDate).getYears();
		} else {
			return 0;
		}
	}

	// TODO Refactor

	/**
	 * Konvertiert Millisekunden in Stunden, Minuten und Sekunden
	 *
	 * @param millis long - Millisekunden
	 * @return String - Stunden + Minuten + Sekunden
	 */
	public static String convertMillisToTime(long millis) {
		long sek = (millis / 1000) % 60;
		long min = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60));

		return hour + " h  " + min + " min  " + sek + " sek";
	}

	/**
	 * Konvertiert Sekunden in Stunden, Minuten und Sekunden
	 *
	 * @param seconds long - Sekunden
	 * @return String - Stunden + Minuten + Sekunden
	 */
	public static String convertSecondsToTime(long seconds) {
		long sek = seconds % 60;
		long min = seconds / 60 % 60;
		long hour = seconds / (60 * 60);

		return hour + " h " + min + " min " + sek + " sek";
	}

	/**
	 * Konvertiert Millis in Minuten und Sekunden
	 *
	 * @param millis long - Sekunden
	 * @return String - Minuten + Sekunden
	 */
	public static String convertMillisToMinutesAndSeconds(long millis) {
		long sek = (millis / 1000) % 60;
		long min = (millis / 1000) / 60;

		return min + ":" + String.format("%02d", sek);
	}

	/**
	 * Konvertiert Millisekunden in Datum und Uhrzeit
	 *
	 * @param millis long - Millisekunden
	 * @return String - dd.MM.yyyy-hh:mm:ss.SSS
	 */
	public static String convertMillisToDateAndTime(long millis) {
		Date date = new Date(millis);

		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		return formatter.format(date);
	}

	/**
	 * Konvertiert einen Timestamp zurück in Millisekunden
	 *
	 * @param time String - Timestamp
	 * @return long - Millisekunden
	 */
	public static long convertTimestampToMillis(String time) {
		try {
			Timestamp timestamp = Timestamp.valueOf(time);
			return timestamp.getTime();
		} catch (IllegalArgumentException e) {
			System.err.println("Falsches Eingabeformat \nString muss folgende Struktur haben: yyyy-mm-dd hh:mm:ss[.SSSSSSSSS]");
		}
		return 0;
	}


}
