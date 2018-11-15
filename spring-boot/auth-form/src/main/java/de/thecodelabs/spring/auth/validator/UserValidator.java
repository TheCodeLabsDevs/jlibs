package de.thecodelabs.spring.auth.validator;

import de.thecodelabs.spring.auth.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

	private static final String PWD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[\\S]{8,}$";

	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		User user = (User) o;

		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			if (!user.getPassword().equals(user.getPasswordConfirm())) {
				errors.rejectValue("password", "NoMatch");
			}
			// At least one number
			if (!user.getPassword().matches(PWD_REGEX)) {
				errors.rejectValue("password", "Requirements");
			}
		}
	}
}
