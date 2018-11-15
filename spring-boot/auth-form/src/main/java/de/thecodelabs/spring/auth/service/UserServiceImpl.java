package de.thecodelabs.spring.auth.service;

import de.thecodelabs.spring.auth.entity.User;
import de.thecodelabs.spring.auth.model.Role;
import de.thecodelabs.spring.auth.model.UserType;
import de.thecodelabs.spring.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, Environment env) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;

		String username = env.getProperty("user.admin.name", "admin");
		String password = env.getProperty("user.admin.password", "password");

		if (getDefaultUser() == null) {
			User admin = new User();
			admin.setUsername(username);
			admin.setPassword(password);
			admin.setPasswordConfirm(password);
			admin.setRole(Role.ADMIN);
			admin.setUserType(UserType.DEFAULT);
			save(admin);

			LOG.info("Create Default User: " + username);
		}
	}

	@Override
	public void save(User user) {
		if (user.getPassword().equals(user.getPasswordConfirm())) {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		userRepository.save(user);
	}

	@Override
	public User getDefaultUser() {
		return userRepository.findByUserType(UserType.DEFAULT);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public boolean canAccessUser(UserDetails userDetails, Long id) {
		User currentUser = findByUsername(userDetails.getUsername());
		return currentUser != null
				&& (currentUser.getRole() == Role.ADMIN || currentUser.getId().equals(id));
	}
}
