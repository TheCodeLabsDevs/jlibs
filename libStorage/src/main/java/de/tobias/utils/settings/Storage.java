package de.tobias.utils.settings;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Storage {

    private static Map<StorageType, StorageHandler> storageHandlerMap;

    static {
        storageHandlerMap = new HashMap<>();

        storageHandlerMap.put(StorageTypes.YAML, new YAMLHandler());
        storageHandlerMap.put(StorageTypes.PROPERTIES, new PropertiesHandler());
    }

    public static <T> T load(InputStream stream, StorageType type, Class<T> clazz) {
        if (!storageHandlerMap.containsKey(type)) {
            throw new IllegalArgumentException("Storage Type unsupported");
        }

        return storageHandlerMap.get(type).deserialize(stream, clazz);
    }

    public static <T> T load(Path path, StorageType type, Class<T> clazz) {
        if (!storageHandlerMap.containsKey(type)) {
            throw new IllegalArgumentException("Storage Type unsupported");
        }

        StorageHandler handler = storageHandlerMap.get(type);
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);

                System.out.println("Create new Config: " + path);
                T instance = clazz.newInstance();
                handler.serialize(path, instance);
                return instance;
            }

            return handler.deserialize(Files.newInputStream(path, StandardOpenOption.READ), clazz);
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"JavaReflectionInvocation", "unchecked"})
    public static <T> T load(StorageType type, Class<T> clazz) {
        Path config = getPath(clazz);

        if (config != null) {
            return load(config, type, clazz);
        } else {
            return null;
        }
    }

    public static <T> void save(Path path, StorageType type, T value) {
        if (!storageHandlerMap.containsKey(type)) {
            throw new IllegalArgumentException("Storage Type unsupported");
        }

        storageHandlerMap.get(type).serialize(path, value);
    }

    public static <T> void save(StorageType type, T value) {
        final Path path = getPath(value.getClass());

        if (path != null) {
            save(path, type, value);
        }
    }

    private static <T> Path getPath(Class<T> clazz) {
        Path config;
        if (clazz.isAnnotationPresent(FilePath.class)) {
            try {
                final FilePath annotation = clazz.getAnnotation(FilePath.class);
                final String filePath = annotation.value();

                if (annotation.absolut()) {
                    return Paths.get(filePath);
                }

                Class<?> appClass = Class.forName("de.tobias.utils.application.App");
                Class pathTypeClass = Class.forName("de.tobias.utils.application.container.PathType");
                Class<?> appUtilsClass = Class.forName("de.tobias.utils.application.ApplicationUtils");
                final Method getApplication = appUtilsClass.getMethod("getApplication");
                final Object app = getApplication.invoke(null);

                final Method getPath = appClass.getMethod("getPath", pathTypeClass, String[].class);
                config = (Path) getPath.invoke(app, Enum.valueOf(pathTypeClass, "CONFIGURATION"), new String[]{filePath});

            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("libUtils not exists");
            }
        } else {
            throw new IllegalArgumentException("Class does not provide FilePath annotation");
        }
        return config;
    }
}
