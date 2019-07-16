package de.thecodelabs.calendar;

import java.time.LocalDateTime;

public interface CalendarProvider
{
	EventCalendar[] queryCalendars();

	Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime);

	Event[] queryEvents(LocalDateTime startDateTime, LocalDateTime endDateTime, String calendar);
}
