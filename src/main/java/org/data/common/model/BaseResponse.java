package org.data.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
	@Builder.Default
	private String msg = "Success";
	@Builder.Default
	private Integer code = 200;
	@Builder.Default
	private String time = System.currentTimeMillis() + "";
	private String traceId;
}
