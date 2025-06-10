package org.data.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchedMatchesDto {
	private int id;
	private String tournamentName;
	private ZonedDateTime kickoffTime;
	private String homeName;
	private String awayName;
	private int homeId;
	private int awayId;
	private Boolean isMatched;
	private String status;
	private ExBetMatchCommonDto.SofaData sofaData;
}
