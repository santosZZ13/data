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
	private boolean isFavorite;
	private String awayName;
	private String status;
	private RoundDto round;
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

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundDto {
		private String roundName;
		private String roundType;
	}
}
