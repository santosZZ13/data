package org.data.util.response;

import lombok.Getter;
import org.data.exception.exceptionHandler.ResponseError;

@Getter
public enum ErrorCodeRegistry {
	INTERNAL_ERROR("INTERNAL_ERROR", "An unexpected error occurred. Please try again later."),
	INVALID_REQUEST("INVALID_REQUEST", "Invalid request parameters"),
	TEAM_NOT_FOUND("TEAM_NOT_FOUND", "Team data not found"),
	EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", "External API error"),
	ANALYSIS_ERROR("ANALYSIS_ERROR", "Failed to process match analysis"),
	INVALID_DATE("INVALID_DATE", "Invalid date format. Expected YYYY-MM-DD");

	private final String code;
	private final String message;

	ErrorCodeRegistry(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResponseError toResponseError() {
		return ResponseError.builder()
				.code(code)
				.message(message)
				.build();
	}

	public ResponseError toResponseError(String customMessage) {
		return ResponseError.builder()
				.code(code)
				.message(customMessage != null ? customMessage : message)
				.build();
	}
}
