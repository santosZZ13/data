package org.data.exBet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetTournamentResponse {
	private Integer sid;
	private Integer tid;
	private Integer cid;
	private String name;
	private Boolean favorite;
	private Integer priority;
	private Integer count;
	private List<ExBetMatchResponse> matches;
}
