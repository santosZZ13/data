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
	private int awayId;
	private String awayName;
	private int homeId;
	private String homeName;
	private String kickoffTime;
	private String tournamentName;
	private Integer sofaMatchId;
	private Boolean isMatched;
	private Integer sofaHomeId;
	private Integer sofaAwayId;
	private String sofaHomeName;
	private String sofaAwayName;
}
