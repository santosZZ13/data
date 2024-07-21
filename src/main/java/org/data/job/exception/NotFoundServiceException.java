package org.data.job.exception;

import org.data.common.exception.ApiException;

public class NotFoundServiceException extends ApiException {
	public NotFoundServiceException(String message, String code, String shortDesc) {
		super(message, code, shortDesc);
	}

	public NotFoundServiceException(String code, String shortDesc, String message, Throwable cause) {
		super(code, shortDesc, message, cause);
	}
}
