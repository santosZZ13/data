package org.data.exception.exceptionHandler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArgumentNotValidResponse {
	private String message;
	private List<FieldErrorResponse> errors;
}
