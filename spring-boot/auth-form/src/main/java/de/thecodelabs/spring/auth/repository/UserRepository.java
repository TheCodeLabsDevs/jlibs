package de.thecodelabs.spring.auth.repository;

import de.thecodelabs.spring.auth.entity.User;
import de.thecodelabs.spring.auth.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	Optional<User> findById(Long id);

	User findByUserType(UserType type);
}