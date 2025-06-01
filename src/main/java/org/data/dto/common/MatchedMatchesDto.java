package org.data.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchedMatchesDto {
	private int id;
	private String tournamentName;
	private String kickoffTime;
	private String homeName;
	private String awayName;
	private int homeId;
	private int awayId;
	private Boolean isMatched;
	private SofaData sofaData;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SofaData {
		private Integer sofaMatchId;
		private Integer sofaHomeId;
		private Integer sofaAwayId;
		private String sofaHomeName;
		private String sofaAwayName;
	}
}
