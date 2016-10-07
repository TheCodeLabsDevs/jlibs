package de.tobias.autostart.impl;

import java.io.File;
import java.util.List;

import de.tobias.autostart.Autostart;
import at.jta.Key;
import at.jta.NotSupportedOSException;
import at.jta.RegistryErrorException;
import at.jta.Regor;

/**
 * Default Autostartimplementation für Windows. Eintrage werden in der Registry
 * gespeichert. Path: HKCU/Software/Microsoft/Windows/CurrentVersion/Run
 * 
 * @author tobias
 * 
 */
public class WindowsAutostart implements Autostart {

	private Regor registry;
	private final String keyname;
	private final Key key;

	public WindowsAutostart() {
		try {
			registry = new Regor();
		} catch (RegistryErrorException | NotSupportedOSException e) {

		} finally {
			keyname = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
			key = new Key(Regor._HKEY_CURRENT_USER, keyname);
		}
	}
	
	@Override
	public String name() {
		return "windows";
	}

	/**
	 * Fügt einen Value zum Key hinzu.
	 */
	@Override
	public void add(String name, File src) throws RegistryErrorException {
		registry.saveValue(key, name, src.getAbsolutePath());
	}

	/**
	 * Überprüft Entry und Value in der Registry.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isAutostart(String name, File src) throws RegistryErrorException {
		List<Object> keys = registry.listKeys(key);
		if (keys.contains(name)) {
			List<Object> values = registry.listValueNames(key);
			if (values.contains(src.getAbsolutePath())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Löscht Entry aus dem Key.
	 */
	@Override
	public void removeAutostart(String name) throws RegistryErrorException {
		registry.deleteEntry(key, name);
	}

}
