package de.thecodelabs.midi.device;

import java.io.Serial;

public class CloseException extends Exception
{
	@Serial
	private static final long serialVersionUID = 1L;

	public CloseException(Throwable cause)
	{
		super(cause);
	}
}
