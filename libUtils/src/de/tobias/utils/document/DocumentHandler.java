package de.tobias.utils.document;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Deklariert eine Methode als Documenthandler zum Speichern eines Models als
 * Formatierte Datei.
 * 
 * @author tobias
 */
@Target({ ElementType.METHOD })
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentHandler {

	/**
	 * Bezeichnet das Dateiformat z.B. PDF
	 * 
	 * @return File Type Extension
	 */
	String type();
}
