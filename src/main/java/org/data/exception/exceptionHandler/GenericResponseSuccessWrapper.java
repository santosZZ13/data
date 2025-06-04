package org.data.exception.exceptionHandler;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GenericResponseSuccessWrapper {
	private Boolean success;
	private Object data;

	public GenericResponseSuccessWrapper() {
		this.success = Boolean.TRUE;
	}

	//	@lombok.Data
//	@AllArgsConstructor
//	@NoArgsConstructor
//	@Builder
//	public static class Data {
//		private String name;
//		private String message;
//		private Integer code;
//		private Integer status;
//	}
}
