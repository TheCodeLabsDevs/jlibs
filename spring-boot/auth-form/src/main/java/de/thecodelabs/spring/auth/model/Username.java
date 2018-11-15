package de.thecodelabs.spring.auth.model;

import de.thecodelabs.spring.auth.entity.User;

public class Username {
	private final String username;
	private final String name;
	private final String lastName;

	public Username(User user) {
		this.username = user.getUsername();
		this.name = user.getName();
		this.lastName = user.getLastName();
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public String getLastName() {
		return lastName;
	}
}
