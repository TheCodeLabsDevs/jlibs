module de.thecodelabs.libCalendar {
	requires de.thecodelabs.libUtils;
	requires commons.httpclient;
	requires caldav4j;
	requires org.mnode.ical4j.core;

	exports de.thecodelabs.calendar;
	exports de.thecodelabs.calendar.caldav;
	exports de.thecodelabs.calendar.eventapi;
	exports de.thecodelabs.calendar.ical;
}