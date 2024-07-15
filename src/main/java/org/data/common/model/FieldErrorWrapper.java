package org.data.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorWrapper {
	private String errorCode;
	private String field;
	private String message;
}
