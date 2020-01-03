package de.thecodelabs.calendar;

public class EventCalendar
{
	private final String title;
	private final String color;

	public EventCalendar(String title, String color)
	{
		this.title = title;
		this.color = color;
	}

	public String getTitle()
	{
		return title;
	}

	public String getColor()
	{
		return color;
	}

	@Override
	public String toString()
	{
		return "EventCalendar{" +
				"title='" + title + '\'' +
				", color='" + color + '\'' +
				'}';
	}
}
