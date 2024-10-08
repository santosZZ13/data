package org.data.response.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetMidsResponse {
	private Integer fmid;
	private Integer bmid;
	private Integer amid;
	private Integer cmid;
	private Integer dmid;
	private Integer jmid;
}
