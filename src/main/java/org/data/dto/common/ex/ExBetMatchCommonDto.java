package org.data.dto.common.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ExBetMatchCommonDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SofaData {
		private Integer sofaMatchId;
		private Integer sofaHomeId;
		private Integer sofaAwayId;
		private String sofaHomeName;
		private String sofaAwayName;
		private ScoreData homeScore;
		private ScoreData awayScore;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class RoundDto {
		private String roundName;
		private String roundType;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScoreData {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normalTime;
		private Integer extra1;
		private Integer extra2;
		private Integer overtime;
		private Integer penalties;
		private Boolean scoreEmpty;
		private Integer aggregated;
	}
}
