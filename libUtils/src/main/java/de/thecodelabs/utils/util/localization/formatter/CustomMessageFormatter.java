package de.thecodelabs.utils.util.localization.formatter;

import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.localization.LocalizationMessageFormatter;

public class CustomMessageFormatter implements LocalizationMessageFormatter
{
	@Override
	public String format(final String source, final Object... args)
	{
		String message = source;
		int index = 0;
		while(message.contains("{}"))
		{
			if(args.length > index)
			{
				if(args[index] != null)
				{
					message = message.replaceFirst("\\{\\}", args[index].toString());
				}
				else
				{
					message = message.replaceFirst("\\{\\}", "null");
				}
				index++;
			}
			else
			{
				LoggerBridge.error("Args invalid: " + message);
				break;
			}
		}
		return message;
	}
}
