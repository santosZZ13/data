package org.data.response.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetTeamResponse {
	private Integer id;
	private Integer cid;
	private String name;
	private ExBetJerseyResponse jersey;
}
