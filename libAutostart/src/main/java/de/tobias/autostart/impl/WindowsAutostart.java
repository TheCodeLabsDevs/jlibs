package de.tobias.autostart.impl;

import com.github.sarxos.winreg.HKey;
import com.github.sarxos.winreg.RegistryException;
import com.github.sarxos.winreg.WindowsRegistry;
import de.tobias.autostart.Autostart;

import java.nio.file.Path;

/**
 * Default Autostartimplementation für Windows. Eintrage werden in der Registry
 * gespeichert. Path: HKCU/Software/Microsoft/Windows/CurrentVersion/Run
 *
 * @author tobias
 */
public class WindowsAutostart implements Autostart
{


	private WindowsRegistry registry;
	private final String keyname;


	public WindowsAutostart()
	{
		registry = WindowsRegistry.getInstance();
		keyname = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
	}

	@Override
	public String name()
	{
		return "Windows";
	}

	/**
	 * Fügt einen Value zum Key hinzu.
	 */
	@Override
	public void add(String name, Path src) throws RegistryException
	{
		registry.writeStringValue(HKey.HKCU, keyname, name, src.toAbsolutePath().toString());
	}

	/**
	 * Überprüft Entry und Value in der Registry.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isAutostart(String name, Path src) throws RegistryException
	{
		String value = registry.readString(HKey.HKCU, keyname, name);

		if (value == null) {
			return false;
		}

		return value.contains(src.toAbsolutePath().toString());
	}

	/**
	 * Löscht Entry aus dem Key.
	 */
	@Override
	public void removeAutostart(String name) throws RegistryException
	{
		registry.deleteValue(HKey.HKCU, keyname, name);
	}

}
