package org.data.job.exception;

import org.data.common.exception.ApiException;

public class JobAlreadyExistException extends ApiException {
	public JobAlreadyExistException(String message, String code, String shortDesc) {
		super(message, code, shortDesc);
	}

	public JobAlreadyExistException(String code, String shortDesc, String message, Throwable cause) {
		super(code, shortDesc, message, cause);
	}
}
