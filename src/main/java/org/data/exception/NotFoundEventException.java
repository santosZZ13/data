package org.data.exception;

import org.data.common.exception.ApiException;

public class NotFoundEventException extends ApiException {
	public NotFoundEventException(String message, String code, String shortDesc) {
		super(message, code, shortDesc);
	}

	public NotFoundEventException(String code, String shortDesc, String message, Throwable cause) {
		super(code, shortDesc, message, cause);
	}
}
