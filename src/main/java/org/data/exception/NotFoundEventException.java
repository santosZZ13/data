package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.annotation.ErrorStatus;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.http.HttpStatus;

@ErrorStatus(HttpStatus.NOT_FOUND)
public class NotFoundEventException extends ApiException {
	public NotFoundEventException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public NotFoundEventException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
