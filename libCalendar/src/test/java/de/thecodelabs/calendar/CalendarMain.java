package de.thecodelabs.calendar;

import de.thecodelabs.calendar.eventapi.MacEventProvider;

public class CalendarMain
{
	public static void main(String[] args)
	{
		CalendarService.setCalendarProvider(new MacEventProvider());

		final CalendarProvider provider = CalendarService.getCalendarProvider();
		final EventCalendar[] eventCalendars = provider.queryCalendars();
		for(EventCalendar eventCalendar : eventCalendars)
		{
			System.out.println(eventCalendar);
		}
	}
}
