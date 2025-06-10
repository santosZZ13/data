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
public class ExBetMatchResponseDto {
	private int id;
	private String tournamentName;
	private long kickoffTime;
	private int homeId;
	private String homeName;
	private int awayId;
	private String awayName;
	private String status;
	private boolean isFavorite;
	private Boolean isMatched;
	private ExBetMatchCommonDto.RoundDto round;
	private ExBetMatchCommonDto.SofaData sofaData;
}
