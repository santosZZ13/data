package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.response.ErrorCodeRegistry;

public class InvalidRequestException extends ApiException {
	public InvalidRequestException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public InvalidRequestException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
