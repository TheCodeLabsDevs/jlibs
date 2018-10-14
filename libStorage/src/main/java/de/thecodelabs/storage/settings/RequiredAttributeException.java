package de.thecodelabs.storage.settings;

import java.lang.reflect.Field;

public class RequiredAttributeException extends Exception {

	private static final long serialVersionUID = 1L;

	public RequiredAttributeException(Field field) {
		super("Field: " + field.getName() + " is required");
	}
}
