package de.thecodelabs.calendar;

import java.time.LocalDateTime;

public class Event
{
	private final String calendar;

	private final String title;
	private final String place;

	private final LocalDateTime startDate;
	private final LocalDateTime endDate;

	public Event(String calendar, String title, String place, LocalDateTime startDate, LocalDateTime endDate)
	{
		this.calendar = calendar;
		this.title = title;
		this.place = place;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getCalendar()
	{
		return calendar;
	}

	public String getTitle()
	{
		return title;
	}

	public String getPlace()
	{
		return place;
	}

	public LocalDateTime getStartDate()
	{
		return startDate;
	}

	public LocalDateTime getEndDate()
	{
		return endDate;
	}

	@Override
	public String toString()
	{
		return "Event{" +
				"calendar='" + calendar + '\'' +
				", title='" + title + '\'' +
				", place='" + place + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				'}';
	}
}
