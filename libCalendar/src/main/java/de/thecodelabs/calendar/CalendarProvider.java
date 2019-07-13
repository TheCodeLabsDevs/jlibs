package de.thecodelabs.calendar;

import java.time.LocalDate;

public interface CalendarProvider
{
	EventCalendar[] queryCalendars();

	Event[] queryEvents(LocalDate startDate, LocalDate endDate);

	Event[] queryEvents(LocalDate startDate, LocalDate endDate, String calendar);
}
