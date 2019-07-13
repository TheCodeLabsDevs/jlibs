package de.thecodelabs.calendar.eventapi;

import de.thecodelabs.calendar.CalendarProvider;
import de.thecodelabs.calendar.Event;
import de.thecodelabs.calendar.EventCalendar;
import de.thecodelabs.utils.application.NativeLoader;
import de.thecodelabs.utils.util.OS;

import java.time.LocalDate;

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

	static {
		loadNativeLibrary();
	}

	public native void initialize();

	@Override
	public EventCalendar[] queryCalendars()
	{
		return queryCalendarApi();
	}

	@Override
	public Event[] queryEvents(LocalDate startDate, LocalDate endDate)
	{
		return queryEventApi(startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear(), endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear());
	}

	@Override
	public Event[] queryEvents(LocalDate startDate, LocalDate endDate, String calendar)
	{
		return queryEventApi(startDate.getDayOfMonth(), startDate.getMonthValue(), startDate.getYear(), endDate.getDayOfMonth(), endDate.getMonthValue(), endDate.getYear(), calendar);
	}

	private native EventCalendar[] queryCalendarApi();

	private native Event[] queryEventApi(int startDate, int startMonth, int startYear, int endDate, int endMonth, int endYear);

	private native Event[] queryEventApi(int startDate, int startMonth, int startYear, int endDate, int endMonth, int endYear, String calendar);
}
