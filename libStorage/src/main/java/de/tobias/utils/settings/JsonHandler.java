package de.tobias.utils.settings;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonHandler implements StorageHandler {
    private Gson createGson() {
        return new GsonBuilder()
                .setFieldNamingStrategy(field -> {
                    if (field.isAnnotationPresent(Key.class)) {
                        Key key = field.getAnnotation(Key.class);
                        if (!key.value().isEmpty()) {
                            return key.value();
                        }
                    }
                    return field.getName();
                })
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        return fieldAttributes.getAnnotation(Key.class) == null;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .setPrettyPrinting()
                .create();
    }

    @Override
    public <T> T deserialize(InputStream stream, Class<T> clazz) {
        return createGson().fromJson(new InputStreamReader(stream), clazz);
    }

    @Override
    public <T> void serialize(Path path, T t) {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }

            final String s = createGson().toJson(t);
            Files.write(path, s.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
