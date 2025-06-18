package org.data.exception.exceptionHandler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.data.util.response.ErrorCodeRegistry;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {
	private final String code;

	public ApiException(String message, ErrorCodeRegistry errorCode) {
		super(message);
		this.code = errorCode.getCode();
	}

	public ApiException(ErrorCodeRegistry errorCode, String message, Throwable cause) {
		super(message, cause);
		this.code = errorCode.getCode();
	}
}
