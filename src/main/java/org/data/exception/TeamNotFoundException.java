package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.response.ErrorCodeRegistry;

public class TeamNotFoundException extends ApiException {
	public TeamNotFoundException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public TeamNotFoundException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
