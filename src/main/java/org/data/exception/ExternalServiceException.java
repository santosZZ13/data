package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.response.ErrorCodeRegistry;

public class ExternalServiceException extends ApiException {
	public ExternalServiceException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public ExternalServiceException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}
