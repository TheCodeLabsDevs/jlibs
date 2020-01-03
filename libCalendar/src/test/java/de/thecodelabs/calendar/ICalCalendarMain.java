package de.thecodelabs.calendar;

import de.thecodelabs.calendar.ical.ICalProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;

public class ICalCalendarMain
{
	public static void main(String[] args) throws MalformedURLException
	{
		CalendarService.setCalendarProvider(new ICalProvider(new URL("https://calendar.google.com/calendar/ical/karatebestensee%40gmail.com/private-b00fbfa99ebaa84726c71408c22ed1ab/basic.ics")));

		final CalendarProvider provider = CalendarService.getCalendarProvider();
		final EventCalendar[] eventCalendars = provider.queryCalendars();
		for(EventCalendar eventCalendar : eventCalendars)
		{
			System.out.println(eventCalendar);
		}

		final Event[] events = provider.queryEvents(LocalDateTime.now().minusDays(60), LocalDateTime.now().plusDays(1));
		System.out.println(Arrays.toString(events));
	}
}
