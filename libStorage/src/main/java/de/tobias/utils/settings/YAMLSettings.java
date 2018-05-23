package de.tobias.utils.settings;

import java.io.File;
import java.io.InputStream;
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
	 * @author tobias
	 */
	protected static class Store {
		private SettingsSerializable serializable;
		private Path file;
	}

	/**
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

	private static void save(Store s) {
		try {
			save(s.serializable, s.file);
			System.out.println("Save: " + s.file);
		} catch (Exception e) {
			System.err.println("Can't save " + s.file);
			e.printStackTrace();
		}
	}

	public static void save(SettingsSerializable serializable, Path file)  {
		try {
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends SettingsSerializable> T load(Class<T> clazz, Path file) {
		try {
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends SettingsSerializable> T load(Class<T> clazz, URL url) {
		try {
			return load(clazz, url.openStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends SettingsSerializable> T load(Class<T> clazz, InputStream iStr) {
		try {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(iStr);
			T serializable = clazz.newInstance();

			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Storable.class))
					if (!Modifier.isFinal(field.getModifiers()))
						if (cfg.isSet(field.getName())) {
							field.set(serializable, cfg.get(field.getName()));
						} else if (field.isAnnotationPresent(Required.class)) {
							throw new RequiredAttributeException(field);
						}
			}
			return serializable;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}