package org.data.eightBet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

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
