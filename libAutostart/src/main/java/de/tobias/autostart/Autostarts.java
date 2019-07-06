package de.tobias.autostart;

import de.thecodelabs.utils.util.OS;
import de.tobias.autostart.impl.LinuxAutoStart;
import de.tobias.autostart.impl.OSXAutoStart;
import de.tobias.autostart.impl.OSXAutoStartNative;
import de.tobias.autostart.impl.WindowsAutostart;

import java.util.HashMap;

/**
 * Standartverwaltung der Autostart API.
 * <p>
 * Standartimplementierungen: Windows, Mac OS X
 *
 * @author tobias
 * @version 1.0
 */
public class Autostarts
{

	private static HashMap<String, Autostart> autostartImplementation;
	private static Autostart autostart;

	static
	{
		try
		{
			autostartImplementation = new HashMap<>();

			// Default Implementation
			addAutostartImplementation(new OSXAutoStart());
			addAutostartImplementation(new OSXAutoStartNative());
			addAutostartImplementation(new WindowsAutostart());
			addAutostartImplementation(new LinuxAutoStart());

			switch(OS.getType())
			{
				case Windows:
					chooseImplementation("Windows");
					break;
				case MacOSX:
					chooseImplementation("MacOS");
					break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Setzt eine eigene Implementionen von Autostart, die dann auch die aktive
	 * ist
	 *
	 * @param autostart
	 */
	public static void addAutostartImplementation(Autostart autostart)
	{
		autostartImplementation.put(autostart.name(), autostart);
	}

	/**
	 * Wählt eine Implementation passend des Suchkriteriums. Standartwählung ist
	 * passend zum OS.
	 *
	 * @param criteria
	 * @throws UnkownAutostartImplementationException
	 */
	public static void chooseImplementation(String criteria) throws UnkownAutostartImplementationException
	{
		for(String name : autostartImplementation.keySet())
		{
			if(name.toLowerCase().equals(criteria.toLowerCase()))
			{
				autostart = autostartImplementation.get(name);
				break;
			}
		}

		if(autostart == null)
			throw new UnkownAutostartImplementationException(criteria);

	}

	/**
	 * Aktive Autostartimplementierung
	 *
	 * @return Implentierung
	 */
	public static Autostart getAutostart()
	{
		return autostart;
	}
}
