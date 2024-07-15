package org.data.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponseWrapper {
	private String msg;
	private String code;
	private Object data;
	private String time;
	private String traceId;
}
