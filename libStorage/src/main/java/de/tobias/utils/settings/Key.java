package de.tobias.utils.settings;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author tobias
 *
 */
@Target({ ElementType.FIELD })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {
	String value() default "";
}