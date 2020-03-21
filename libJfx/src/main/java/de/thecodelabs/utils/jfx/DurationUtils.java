package de.thecodelabs.utils.jfx;

import javafx.util.Duration;

import java.util.Optional;

public class DurationUtils
{
	private DurationUtils()
	{
	}

	public static Optional<Duration> parseDuration(String input)
	{
		if(!input.endsWith("s"))
		{
			input += "s";
		}
		input = input.replace(" ", "").replace(",", ".");
		if(input.matches("\\d+(\\.\\d+)s") || input.matches("\\d+s"))
		{
			return Optional.of(Duration.valueOf(input));
		}
		return Optional.empty();
	}
}
