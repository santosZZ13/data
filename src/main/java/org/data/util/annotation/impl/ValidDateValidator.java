package org.data.util.annotation.impl;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.data.util.annotation.ValidDate;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {
	private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {
		if (date == null || !date.matches(DATE_PATTERN)) {
			return false;
		}
		return true;
	}
}
