package org.data.common.exception;

import lombok.extern.log4j.Log4j2;
import org.data.common.model.FieldErrorWrapper;
import org.data.common.model.GenericResponseWrapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
@Log4j2
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

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

		return ResponseEntity.badRequest().body(GenericResponseWrapper
				.builder()
				.data(fieldErrorWrappers)
				.code("")
				.msg("")
				.build());
	}

	@ExceptionHandler({ApiException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handlerApiException(@NotNull ApiException ex, WebRequest request) {
		String message = ex.getMessage();
		String code = ex.getCode();
		String shortDesc = ex.getShortDesc();

		GenericResponseWrapper response = GenericResponseWrapper.builder()
				.code(code)
				.msg(shortDesc)
				.data(message)
				.build();

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handAll(Exception ex, WebRequest request) {
		log.error("Exception occurred: ", ex);
		GenericResponseWrapper response = GenericResponseWrapper.builder()
				.code("INTERNAL_SERVER_ERROR")
				.msg("Request could not be processed due to an internal error")
				.build();
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}



	private String getErrorCode(Object[] arguments) {
		if (Objects.nonNull(arguments) && arguments.length > 0) {
			return arguments[1].toString();
		}
		return null;
	}
}
