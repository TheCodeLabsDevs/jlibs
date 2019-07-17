package de.thecodelabs.calendar;

import de.thecodelabs.calendar.caldav.CalDavProvider;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class CalDAVCalendarMain
{
	public static void main(String[] args) throws MalformedURLException
	{
		String password = args[0];
		CalendarService.setCalendarProvider(new CalDavProvider("owncloud.thecodelabs.de", 443, "https", "tobias", password, "/remote.php/dav/calendars/tobias/personal/"));

		final CalendarProvider provider = CalendarService.getCalendarProvider();
		final EventCalendar[] eventCalendars = provider.queryCalendars();
		for(EventCalendar eventCalendar : eventCalendars)
		{
			System.out.println(eventCalendar);
		}

		final Event[] events = provider.queryEvents(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
		System.out.println(Arrays.toString(events));
	}
}
