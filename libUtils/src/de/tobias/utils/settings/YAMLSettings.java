package de.tobias.utils.settings;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YAMLSettings {

	/**
	 * 
	 */
	private static List<Store> stores = new ArrayList<>();

	/**
	 * 
	 * @author tobias
	 *
	 */
	protected static class Store {
		private SettingsSerializable serializable;
		private Path file;
	}

	/**
	 * 
	 * @param serializable
	 * @param file
	 */
	@Deprecated
	public static void addSettings(SettingsSerializable serializable, File file) {
		Store s = new Store();
		s.file = file.toPath();
		s.serializable = serializable;
		stores.add(s);
	}

	/**
	 * 
	 * @param serializable
	 * @param file
	 */
	public static void addSettings(SettingsSerializable serializable, Path file) {
		Store s = new Store();
		s.file = file;
		s.serializable = serializable;
		stores.add(s);
	}

	/**
	 * 
	 */
	public static void saveAll() {
		stores.forEach(YAMLSettings::save);
	}

	/**
	 * 
	 * @param s
	 */
	private static void save(Store s) {
		try {
			save(s.serializable, s.file);
			System.out.println("Save: " + s.file);
		} catch (Exception e) {
			System.err.println("Can't save " + s.file);
			e.printStackTrace();
		}
	}

	@Deprecated
	public static void save(SettingsSerializable serializable, File file) throws Exception {
		save(serializable, file.toPath());
	}

	public static void save(SettingsSerializable serializable, Path file) throws Exception {
		if (Files.notExists(file)) {
			Files.createDirectories(file.getParent());
			Files.createFile(file);
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(Files.newInputStream(file, StandardOpenOption.READ));

		for (Field field : serializable.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Storable.class)) {
				cfg.set(field.getName(), field.get(serializable));
			}
		}
		cfg.save(file.toString());
	}

	@Deprecated
	public static Serializable load(Class<? extends SettingsSerializable> clazz, File file) throws Exception {
		return load(clazz, file.toPath());
	}

	public static <T extends SettingsSerializable> T load(Class<T> clazz, Path file) throws Exception {
		if (Files.notExists(file)) {
			Files.createDirectories(file.getParent());
			Files.createFile(file);

			// Create Config
			System.out.println("Create new Config: " + file);
			SettingsSerializable instanze = clazz.newInstance();
			YAMLSettings.save(instanze, file);
		}

		FileConfiguration cfg = YamlConfiguration.loadConfiguration(Files.newInputStream(file, StandardOpenOption.READ));
		T serializable = clazz.newInstance();

		boolean added = false;

		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Storable.class))
				if (!Modifier.isFinal(field.getModifiers()))
					if (cfg.isSet(field.getName())) {
						field.set(serializable, cfg.get(field.getName()));
					} else {
						cfg.set(field.getName(), field.get(serializable));
						added = true;
					}
		}
		if (added) {
			cfg.save(file.toString());
		}
		return serializable;
	}

	public static <T extends SettingsSerializable> T load(Class<T> clazz, URL url) throws Exception {
		return load(clazz, url.openStream());
	}
	
	public static <T extends SettingsSerializable> T load(Class<T> clazz, InputStream iStr) throws Exception {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(iStr);
		T serializable = clazz.newInstance();

		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Storable.class))
				if (!Modifier.isFinal(field.getModifiers()))
					if (cfg.isSet(field.getName()))
						field.set(serializable, cfg.get(field.getName()));
					else if (field.isAnnotationPresent(Required.class)) {
						throw new RequirdedAttributeException(field);
					}
		}
		return serializable;
	}
}
