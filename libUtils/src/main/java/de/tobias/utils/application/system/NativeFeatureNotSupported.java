package de.tobias.utils.application.system;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NativeFeatureNotSupported {
}
