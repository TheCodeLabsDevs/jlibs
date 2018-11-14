package de.thecodelabs.spring.auth.controller;

import de.thecodelabs.spring.auth.entity.User;
import de.thecodelabs.spring.auth.service.UserService;
import de.thecodelabs.spring.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/de/thecodelabs/spring/auth")
public class UserController
{

	private final UserValidator userValidator;
	private final UserService userService;

	@Autowired
	public UserController(UserService userService, UserValidator userValidator)
	{
		this.userService = userService;
		this.userValidator = userValidator;
	}

	@RequestMapping(value = "/app/user/", method = RequestMethod.GET)
	public String getUser(Principal principal, Model model)
	{
		if(principal instanceof UsernamePasswordAuthenticationToken)
		{
			UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) principal;
			model.addAttribute("user", userService.findByUsername(user.getName()));
		}
		else
		{
			RememberMeAuthenticationToken user = (RememberMeAuthenticationToken) principal;
			model.addAttribute("user", userService.findByUsername(user.getName()));
		}
		return "user";
	}

	@PreAuthorize("@userServiceImpl.canAccessUser(principal, #id)")
	@RequestMapping(value = "/app/user/{id}", method = RequestMethod.GET)
	public String getUser(@PathVariable Long id, Model model)
	{
		model.addAttribute("user", userService.findById(id));
		return "user";
	}

	@PreAuthorize("@userServiceImpl.canAccessUser(principal, #id)")
	@RequestMapping(value = "/app/user/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long id)
	{
		userService.deleteUser(id);
	}

	@RequestMapping(value = "/app/user/", method = RequestMethod.POST)
	public String updateUser(Principal principal, @ModelAttribute("user") User form, BindingResult bindingResult, Model model)
	{
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
		User user = userService.findByUsername(token.getName());
		handleUserPost(form, user, bindingResult, model);
		if(bindingResult.hasErrors())
		{
			return "user";
		}
		return "redirect:/app/user/";
	}

	@PreAuthorize("@userServiceImpl.canAccessUser(principal, #id)")
	@RequestMapping(value = "/app/user/{id}", method = RequestMethod.POST)
	public String updateUser(Principal principal, @PathVariable Long id, @ModelAttribute("user") User form, BindingResult bindingResult, Model model)
	{
		Optional<User> user = userService.findById(id);
		if(user.isPresent())
		{
			handleUserPost(form, user.get(), bindingResult, model);
			if(bindingResult.hasErrors())
			{
				return "user";
			}
		}
		return "redirect:/app/users/";
	}

	private void handleUserPost(User form, User user, BindingResult bindingResult, Model model)
	{
		form.setUsername(user.getUsername());

		userValidator.validate(form, bindingResult);
		if(bindingResult.hasErrors())
		{
			model.addAttribute("error", bindingResult);
			return;
		}

		if(form.getPassword() != null && !form.getPassword().isEmpty())
		{
			user.setPassword(form.getPassword());
			user.setPasswordConfirm(form.getPasswordConfirm());
		}

		user.setName(form.getName());
		user.setLastName(form.getLastName());

		userService.save(user);

		model.addAttribute("user", user);
	}
}
