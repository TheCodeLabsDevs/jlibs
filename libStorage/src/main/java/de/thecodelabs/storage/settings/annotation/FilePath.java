package de.thecodelabs.storage.settings.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilePath
{
    /**
     * File path for the file
     *
     * @return path for settings class
     */
    String value();

    boolean absolute() default false;
}
