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
public class ExBetMatchDto {
	private int id;
	private String tournamentName;
	private ZonedDateTime kickoffTime;
	private int homeId;
	private String homeName;
	private int awayId;
	private String awayName;
	private String status;
	private ExBetMatchCommonDto.RoundDto round;
	private boolean isFavorite;
//	private Boolean isMatched;
//	private ExBetMatchCommonDto.SofaData sofaData;
}
