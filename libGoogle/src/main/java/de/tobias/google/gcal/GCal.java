package de.tobias.google.gcal;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import de.tobias.google.Authentication;
import de.tobias.google.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GCal extends Service {

	private Calendar service;

	@Override
	protected void init(Authentication authentication, String appName) {
		service = new Calendar.Builder(authentication.getHTTP_TRANSPORT(), authentication.getJSON_FACTORY(), authentication.getCredential())
				.setApplicationName(appName).build();
	}

	public List<CalendarListEntry> getCalendars() throws IOException {
		List<CalendarListEntry> list = new ArrayList<>();
		String pageToken = null;
		do {
			CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
			List<CalendarListEntry> items = calendarList.getItems();

			for (CalendarListEntry calendarListEntry : items) {
				list.add(calendarListEntry);
			}
			pageToken = calendarList.getNextPageToken();
		} while (pageToken != null);
		return list;
	}

	public CalendarListEntry getCalendar(String id) throws IOException {
		List<CalendarListEntry> list = getCalendars();
		for (CalendarListEntry entry : list) {
			if (entry.getId().equals(id)) {
				return entry;
			}
		}
		return null;
	}

	public Colors getColor() throws IOException {
		Colors colors = service.colors().get().execute();
		return colors;
	}

	public Event insertEvent(Event event, CalendarListEntry calendar) throws IOException {
		return service.events().insert(calendar.getId(), event).execute();
	}

	public void deleteEvent(Event event, CalendarListEntry calendar) throws IOException {
		service.events().delete(calendar.getId(), event.getId()).execute();
	}

	public Event updateEvent(Event event, CalendarListEntry calendar) throws IOException {
		return service.events().update(calendar.getId(), event.getId(), event).execute();
	}

	public Event moveEvent(Event event, CalendarListEntry oldCal, CalendarListEntry newCal) throws IOException {
		return service.events().move(oldCal.getId(), event.getId(), newCal.getId()).execute();
	}

	public List<Event> getEvents(CalendarListEntry calendar) throws IOException {
		List<Event> list = new ArrayList<>();
		String pageToken = null;
		do {
			Events events = service.events().list(calendar.getId()).setPageToken(pageToken).execute();
			List<Event> items = events.getItems();
			for (Event event : items) {
				list.add(event);
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
		return list;
	}

	public static String[] scope = {CalendarScopes.CALENDAR};

}
