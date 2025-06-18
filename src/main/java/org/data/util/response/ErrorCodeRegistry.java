package org.data.util.response;

import lombok.Getter;
import org.data.exception.exceptionHandler.ResponseError;

import java.util.Locale;
import java.util.Map;

@Getter
public enum ErrorCodeRegistry {
	INTERNAL_ERROR("INTERNAL_ERROR", Map.of(
			Locale.ENGLISH, "An unexpected error occurred. Please try again later.",
			new Locale("vi"), "Lỗi không mong muốn xảy ra. Vui lòng thử lại sau."
	)),
	INVALID_REQUEST("INVALID_REQUEST", Map.of(
			Locale.ENGLISH, "Invalid request parameters",
			new Locale("vi"), "Tham số yêu cầu không hợp lệ"
	)),
	NOT_FOUND_EVENT("NOT_FOUND_EVENT", Map.of(
			Locale.ENGLISH, "Event data not found",
			new Locale("vi"), "Không tìm thấy dữ liệu sự kiện"
	)),
	EXTERNAL_SERVICE_ERROR("EXTERNAL_SERVICE_ERROR", Map.of(
			Locale.ENGLISH, "Failed to fetch data from external service",
			new Locale("vi"), "Lấy dữ liệu từ dịch vụ bên ngoài thất bại"
	)),
	ANALYSIS_ERROR("ANALYSIS_ERROR", Map.of(
			Locale.ENGLISH, "Failed to process match analysis",
			new Locale("vi"), "Xử lý phân tích trận đấu thất bại"
	)),
	INVALID_DATE("INVALID_DATE", Map.of(
			Locale.ENGLISH, "Invalid date format. Expected YYYY-MM-DD",
			new Locale("vi"), "Định dạng ngày không hợp lệ. Kỳ vọng YYYY-MM-DD"
	));

	private final String code;
	private final Map<Locale, String> messages;

	ErrorCodeRegistry(String code, Map<Locale, String> messages) {
		this.code = code;
		this.messages = messages;
	}

	public ResponseError toResponseError(Locale locale) {
		String message = messages.getOrDefault(locale, messages.get(Locale.ENGLISH));
		return ResponseError.builder()
				.code(code)
				.message(message)
				.build();
	}

	public ResponseError toResponseError(String customMessage) {
		return ResponseError.builder()
				.code(code)
				.message(customMessage != null ? customMessage : messages.get(Locale.ENGLISH))
				.build();
	}

	public String getMessage(Locale locale) {
		return messages.getOrDefault(locale, messages.get(Locale.ENGLISH));
	}
}
