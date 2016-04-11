package de.tobias.utils.settings;

import java.lang.reflect.Field;

public class RequirdedAttributeException extends Exception {

	private static final long serialVersionUID = 1L;

	public RequirdedAttributeException(Field field) {
		super("Field: " + field.getName() + " is required");
	}
}
