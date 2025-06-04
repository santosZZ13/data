package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusTimeResponse {
	private String prefix;
	private Integer initial;
	private Integer max;
	private Long timestamp;
	private Integer extra;
}
