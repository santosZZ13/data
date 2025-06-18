package org.data.exception;

import org.data.exception.exceptionHandler.ApiException;
import org.data.util.response.ErrorCodeRegistry;

public class AnalysisProcessingException extends ApiException {
	public AnalysisProcessingException(String message, ErrorCodeRegistry errorCode) {
		super(message, errorCode);
	}

	public AnalysisProcessingException(String message, ErrorCodeRegistry errorCode, Throwable cause) {
		super(errorCode, message, cause);
	}
}
