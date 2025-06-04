package org.data.exception.exceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handAll(@NotNull Exception ex, WebRequest request, HttpServletResponse response) {
		ResponseError responseError = ResponseError.builder()
				.code("")
				.shortDesc(ex.getMessage())
				.message(ex.getMessage())
				.build();
		return new ResponseEntity<>(
				responseError,
				new HttpHeaders(),
				INTERNAL_SERVER_ERROR
		);
	}


	@ExceptionHandler({ApiException.class})
	public ResponseEntity<Object> handlerApiException(@NotNull ApiException ex, WebRequest request) {
		final String code = ex.getCode();
		final String shortDesc = ex.getShortDesc();
		final String message = ex.getMessage();
		ResponseError responseError = ResponseError.builder()
				.code(code)
				.shortDesc(shortDesc)
				.message(message)
				.build();
		return new ResponseEntity<>(GenericResponseSuccessWrapper.builder()
				.success(Boolean.FALSE)
				.data(responseError)
				.build(), new HttpHeaders(), BAD_REQUEST);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex,
																  @NotNull HttpHeaders headers,
																  @NotNull HttpStatusCode status, @NotNull WebRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		List<FieldError> fieldErrors = bindingResult.getFieldErrors();
		List<FieldErrorWrapper> fieldErrorWrappers = new ArrayList<>();

		fieldErrors.forEach(fieldError -> {
			FieldErrorWrapper fieldErrorWrapper = new FieldErrorWrapper();
			String errorCode = getErrorCode(fieldError.getArguments());
			fieldErrorWrapper.setErrorCode(errorCode);
			fieldErrorWrapper.setField(fieldError.getField());
			fieldErrorWrapper.setMessage(fieldError.getDefaultMessage());
			fieldErrorWrappers.add(fieldErrorWrapper);
		});

		ResponseEntity<GenericResponseErrorWrapper> validationFailed = ResponseEntity.badRequest().body(GenericResponseErrorWrapper
				.builder()
				.errors(fieldErrorWrappers)
				.message("Validation failed")
				.build());

		return new ResponseEntity<>(validationFailed, new HttpHeaders(), BAD_REQUEST);
	}

	private String getErrorCode(Object[] arguments) {
		if (Objects.nonNull(arguments) && arguments.length > 0) {
			return arguments[1].toString();
		}
		return null;
	}
}
