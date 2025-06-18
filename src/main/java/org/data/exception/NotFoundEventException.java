package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.response.ErrorCodeRegistry;

public class NotFoundEventException extends ApiException {
	public NotFoundEventException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public NotFoundEventException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
