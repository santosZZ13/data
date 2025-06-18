package org.data.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.data.util.annotation.impl.ValidDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidDateValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDate {
	String message() default "Invalid date format. Expected YYYY-MM-DD.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
