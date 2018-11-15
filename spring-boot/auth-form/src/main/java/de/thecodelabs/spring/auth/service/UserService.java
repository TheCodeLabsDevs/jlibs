package de.thecodelabs.spring.auth.service;

import de.thecodelabs.spring.auth.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
	void save(User user);

	User findByUsername(String username);

	Optional<User> findById(Long id);

	User getDefaultUser();

	List<User> getAllUsers();

	void deleteUser(Long id);
}
