package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.annotation.ErrorStatus;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.http.HttpStatus;

@ErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AnalysisProcessingException extends ApiException {
	public AnalysisProcessingException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public AnalysisProcessingException(String message, ErrorCodeRegistry errorCode, Throwable cause) {
		super(errorCode, message, cause);
	}
}
