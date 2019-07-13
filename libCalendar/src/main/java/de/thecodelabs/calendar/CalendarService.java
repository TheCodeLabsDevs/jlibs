package de.thecodelabs.calendar;

public class CalendarService
{
	private static CalendarProvider calendarProvider;

	private CalendarService()
	{
	}

	public static CalendarProvider getCalendarProvider()
	{
		return calendarProvider;
	}

	public static void setCalendarProvider(CalendarProvider calendarProvider)
	{
		CalendarService.calendarProvider = calendarProvider;
	}
}
