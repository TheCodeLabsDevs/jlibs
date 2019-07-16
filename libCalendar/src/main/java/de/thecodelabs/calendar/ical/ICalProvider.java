package de.thecodelabs.calendar.ical;

import de.thecodelabs.calendar.CalendarProvider;
import de.thecodelabs.calendar.Event;
import de.thecodelabs.calendar.EventCalendar;
import de.thecodelabs.utils.io.IOUtils;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ICalProvider implements CalendarProvider
{
	static
	{
		System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");
	}

	private URL url;

	public ICalProvider(URL url)
	{
		this.url = url;
	}

	@Override
	public EventCalendar[] queryCalendars()
	{
		return new EventCalendar[0];
	}

	@Override
	public Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime)
	{
		List<Event> events = new ArrayList<>();
		try
		{
			byte[] fin = IOUtils.urlToByteArray(url);
			CalendarBuilder builder = new CalendarBuilder();
			Calendar calendar = builder.build(new ByteArrayInputStream(fin));

			final DateTime startDate = new DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			final DateTime endDate = new DateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

			Period period = new Period(startDate, endDate);
			Filter<CalendarComponent> filter = new Filter<>(new PeriodRule<>(period));

			Collection<CalendarComponent> eventsToday = filter.filter(calendar.getComponents(Component.VEVENT));
			for(CalendarComponent component : eventsToday)
			{
				if(component instanceof VEvent)
				{
					VEvent event = (VEvent) component;

					String title = event.getSummary().getValue();
					String place = event.getLocation().getValue();

					LocalDateTime eventStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getStartDate().getDate().getTime()), ZoneId.systemDefault());
					LocalDateTime eventEndTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getEndDate().getDate().getTime()), ZoneId.systemDefault());

					Event e = new Event(null, title, place, eventStartTime, eventEndTime);
					events.add(e);
				}
			}
		}
		catch(IOException | ParserException e)
		{
			throw new RuntimeException(e);
		}
		return events.toArray(new Event[0]);
	}

	@Override
	public Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime, String calendar)
	{
		return queryEvents(startDateTime, endDateTime);
	}
}
