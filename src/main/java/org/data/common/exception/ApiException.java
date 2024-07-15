package org.data.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApiException extends RuntimeException {
	private final String code;
	private final String shortDesc;

	public ApiException(String message, String code, String shortDesc) {
		super(message);
		this.code = code;
		this.shortDesc = shortDesc;
	}

	public ApiException(String code, String shortDesc, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.shortDesc = shortDesc;
	}
}
