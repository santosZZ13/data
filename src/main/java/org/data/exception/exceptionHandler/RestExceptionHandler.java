package org.data.exception.exceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@Log4j2
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handAll(Exception ex,
										  WebRequest request,
										  HttpServletResponse response) {
		log.error("Unexpected error occurred", ex);
		ResponseError responseError = ResponseError.builder()
				.code("INTERNAL_ERROR")
				.message("An unexpected error occurred. Please try again later.")
				.build();
		return new ResponseEntity<>(responseError, new HttpHeaders(), INTERNAL_SERVER_ERROR);
	}


	@ExceptionHandler({ApiException.class})
	public ResponseEntity<Object> handlerApiException(@NotNull ApiException ex, WebRequest request) {
		final String code = ex.getCode();
		final String message = ex.getMessage();
		ResponseError responseError = ResponseError.builder()
				.code(code)
				.message(message)
				.build();
		return new ResponseEntity<>(responseError, new HttpHeaders(), BAD_REQUEST);
	}


	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
																  @NotNull HttpHeaders headers,
																  @NotNull HttpStatusCode status, @NotNull WebRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
		List<FieldErrorResponse> fieldErrorResponseWrappers = new ArrayList<>();
		fieldErrors.forEach(fieldError -> {
			FieldErrorResponse fieldErrorResponseWrapper = new FieldErrorResponse();
			String errorCode = getErrorCode(fieldError.getArguments());
			fieldErrorResponseWrapper.setErrorCode(errorCode);
			fieldErrorResponseWrapper.setField(fieldError.getField());
			fieldErrorResponseWrapper.setMessage(fieldError.getDefaultMessage());
			fieldErrorResponseWrappers.add(fieldErrorResponseWrapper);
		});

		log.warn("Validation failed: {}", fieldErrorResponseWrappers);
		return new ResponseEntity<>(ArgumentNotValidResponse.builder()
				.errors(fieldErrorResponseWrappers)
				.message("Validation failed")
				.build(), new HttpHeaders(), BAD_REQUEST);
	}

	private String getErrorCode(Object[] arguments) {
		if (Objects.nonNull(arguments) && arguments.length > 0) {
			return arguments[1].toString();
		}
		return null;
	}
}
