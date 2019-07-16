package de.thecodelabs.calendar.caldav;

import de.thecodelabs.calendar.CalendarProvider;
import de.thecodelabs.calendar.Event;
import de.thecodelabs.calendar.EventCalendar;
import de.thecodelabs.calendar.ical.ICalProvider;
import de.thecodelabs.utils.logger.LoggerBridge;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.osaf.caldav4j.CalDAVCollection;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.util.GenerateQuery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CalDavProvider implements CalendarProvider
{
	static
	{
		System.setProperty("net.fortuna.ical4j.timezone.cache.impl", "net.fortuna.ical4j.util.MapTimeZoneCache");
	}

	private String host;
	private int port;
	private String protocol;

	private String username;
	private String password;

	private String path;

	public CalDavProvider(String host, int port, String protocol, String username, String password, String path)
	{
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.username = username;
		this.password = password;
		this.path = path;
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
			HttpClient httpClient = new HttpClient();
			// I tried it with zimbra - but I had no luck using google calendar
			httpClient.getHostConfiguration().setHost(host, port, protocol);
			UsernamePasswordCredentials httpCredentials = new UsernamePasswordCredentials(username, password);
			httpClient.getState().setCredentials(AuthScope.ANY, httpCredentials);
			httpClient.getParams().setAuthenticationPreemptive(true);

			CalDAVCollection collection = new CalDAVCollection(
					path,
					(HostConfiguration) httpClient.getHostConfiguration().clone(),
					new CalDAV4JMethodFactory(),
					CalDAVConstants.PROC_ID_DEFAULT
			);

			final DateTime startDate = new DateTime(startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
			final DateTime endDate = new DateTime(endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

			GenerateQuery gq = new GenerateQuery();
			gq.setFilter("VEVENT");
			gq.setTimeRange(startDate, endDate);
			LoggerBridge.trace("Query: " + gq.prettyPrint());

			CalendarQuery calendarQuery = gq.generate();
			List<Calendar> calendars = collection.queryCalendars(httpClient, calendarQuery);

			for(Calendar calendar : calendars)
			{
				ComponentList<VEvent> componentList = calendar.getComponents().getComponents(Component.VEVENT);
				for(VEvent ve : componentList)
				{
					events.add(ICalProvider.getEvent(ve));
				}
			}

		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return events.toArray(new Event[0]);
	}

	@Override
	public Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime, String calendar)
	{
		return new Event[0];
	}
}
