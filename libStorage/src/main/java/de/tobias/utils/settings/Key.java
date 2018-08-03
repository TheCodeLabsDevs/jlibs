package de.tobias.utils.settings;

import java.lang.annotation.*;

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
