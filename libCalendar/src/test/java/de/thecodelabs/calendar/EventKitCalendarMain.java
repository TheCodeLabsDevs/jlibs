package de.thecodelabs.calendar;

import de.thecodelabs.calendar.eventapi.MacEventProvider;

import java.time.LocalDateTime;
import java.util.Arrays;

public class EventKitCalendarMain
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

		final Event[] events = provider.queryEvents(LocalDateTime.now(), LocalDateTime.now().plusDays(1), "Kalender", "Studium");
		System.out.println(Arrays.toString(events));
	}
}
