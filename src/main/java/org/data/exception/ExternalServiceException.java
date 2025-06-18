package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.annotation.ErrorStatus;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.http.HttpStatus;

@ErrorStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalServiceException extends ApiException {
	public ExternalServiceException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public ExternalServiceException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
