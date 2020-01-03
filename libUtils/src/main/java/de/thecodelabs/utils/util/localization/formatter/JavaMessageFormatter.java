package de.thecodelabs.utils.util.localization.formatter;

import de.thecodelabs.utils.util.localization.LocalizationMessageFormatter;

import java.text.MessageFormat;

public class JavaMessageFormatter implements LocalizationMessageFormatter
{
	@Override
	public String format(final String source, final Object... args)
	{
		return MessageFormat.format(source, args);
	}
}
