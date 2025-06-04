package org.data.exception.exceptionHandler;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class GenericResponseErrorWrapper {
	private String message;
	private List<FieldErrorWrapper> errors;

	public GenericResponseErrorWrapper(String message) {
		this.message = message;
	}

	public GenericResponseErrorWrapper(String message, List<FieldErrorWrapper> errors) {
		this.message = message;
		this.errors = errors;
	}

	public GenericResponseErrorWrapper(String message, FieldErrorWrapper error) {
		super();
		this.message = message;
		errors = Collections.singletonList(error);
	}
}
