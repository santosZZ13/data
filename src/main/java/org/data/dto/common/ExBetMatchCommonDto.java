package org.data.dto.common;

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
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class RoundDto {
		private String roundName;
		private String roundType;
	}
}
