package org.data.exception.exceptionHandler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorResponse {
	private String errorCode;
	private String field;
	private String message;
}
