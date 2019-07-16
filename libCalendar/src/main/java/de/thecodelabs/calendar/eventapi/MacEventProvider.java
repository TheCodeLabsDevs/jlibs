package de.thecodelabs.calendar.eventapi;

import de.thecodelabs.calendar.CalendarProvider;
import de.thecodelabs.calendar.Event;
import de.thecodelabs.calendar.EventCalendar;
import de.thecodelabs.utils.application.NativeLoader;
import de.thecodelabs.utils.util.OS;

import java.time.LocalDateTime;

public class MacEventProvider implements CalendarProvider
{
	private static boolean loaded = false;

	private static void loadNativeLibrary()
	{
		if(!loaded && OS.isMacOS())
		{
			NativeLoader.load("liblibCalendarNative.dylib", "de/thecodelabs/calendar/library", MacEventProvider.class);
			loaded = !loaded;
		}
	}

	static
	{
		loadNativeLibrary();
	}

	public native void initialize();

	@Override
	public EventCalendar[] queryCalendars()
	{
		return queryCalendarApi();
	}

	@Override
	public Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime)
	{
		return queryEventApi(startDateTime.getDayOfMonth(), startDateTime.getMonthValue(), startDateTime.getYear(), startDateTime.getHour(), startDateTime.getMinute(),
				endDateTime.getDayOfMonth(), endDateTime.getMonthValue(), endDateTime.getYear(), endDateTime.getHour(), endDateTime.getMinute());
	}

	@Override
	public Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime, String calendar)
	{
		return queryEventApi(startDateTime.getDayOfMonth(), startDateTime.getMonthValue(), startDateTime.getYear(), startDateTime.getHour(), startDateTime.getMinute(),
				endDateTime.getDayOfMonth(), endDateTime.getMonthValue(), endDateTime.getYear(), endDateTime.getHour(), endDateTime.getMinute(), calendar);
	}

	private native EventCalendar[] queryCalendarApi();

	private native Event[] queryEventApi(int startDate, int startMonth, int startYear, int startHour, int startMinute,
										 int endDate, int endMonth, int endYear, int endHour, int endMinute);

	private native Event[] queryEventApi(int startDate, int startMonth, int startYear, int startHour, int startMinute,
										 int endDate, int endMonth, int endYear, int endHour, int endMinute, String calendar);
}
