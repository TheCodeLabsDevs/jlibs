package de.thecodelabs.spring.auth.controller;

import de.thecodelabs.spring.auth.entity.User;
import de.thecodelabs.spring.auth.model.Username;
import de.thecodelabs.spring.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserControllerAdvice {

	private final UserService userService;

	@Autowired
	public CurrentUserControllerAdvice(UserService userService) {
		this.userService = userService;
	}

	@ModelAttribute("currentUser")
	public UserDetails getCurrentUser(Authentication authentication) {
		return (authentication == null) ? null : (UserDetails) authentication.getPrincipal();
	}

	@ModelAttribute("currentAccount")
	public Username getCurrentAccount(Authentication authentication) {
		if (authentication != null) {
			UserDetails principal = (UserDetails) authentication.getPrincipal();
			User user = userService.findByUsername(principal.getUsername());
			if (user == null) {
				return null;
			}
			return new Username(user);
		} else {
			return null;
		}
	}

}
