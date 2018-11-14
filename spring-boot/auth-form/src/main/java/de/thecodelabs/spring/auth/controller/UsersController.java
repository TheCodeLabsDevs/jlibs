package de.thecodelabs.spring.auth.controller;

import de.thecodelabs.spring.auth.entity.User;
import de.thecodelabs.spring.auth.service.UserService;
import de.thecodelabs.spring.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/de/thecodelabs/spring/auth")
public class UsersController {

	private final UserService userService;
	private final UserValidator userValidator;

	@Autowired
	public UsersController(UserService userService, UserValidator userValidator) {
		this.userService = userService;
		this.userValidator = userValidator;
	}

	@RequestMapping(value = "/app/users/", method = RequestMethod.GET)
	public String usersList(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "users";
	}


	@RequestMapping(value = "/app/users/add", method = RequestMethod.GET)
	public String mobileAddUser() {
		return "mobile/user/userAdd";
	}

	@RequestMapping(value = "/app/users/", method = RequestMethod.POST)
	public String userCreate(Model model, @ModelAttribute("user") User user, BindingResult bindingResult) {
		userValidator.validate(user, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("error", true);
			model.addAttribute("users", userService.getAllUsers());
			return "users";
		}

		if (user.getName().isEmpty()) {
			user.setName(null);
		}

		if (user.getLastName().isEmpty()) {
			user.setLastName(null);
		}

		userService.save(user);

		model.addAttribute("users", userService.getAllUsers());
		return "users";
	}
}
