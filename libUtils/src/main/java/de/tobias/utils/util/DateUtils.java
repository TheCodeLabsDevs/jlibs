package de.tobias.utils.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

	public static LocalDate parse(String input) throws ParseException {
		input = input.replace(" ", "");
		Date date = dateFormat.parse(input);
		return toLocalDate(date);
	}

	public static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDate toLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
