package de.thecodelabs.spring.auth.model;

public enum Role {
	ADMIN, USER;

	@Override
	public String toString() {
		return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
	}
}
