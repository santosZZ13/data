package org.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public interface GetMatchesExByDate {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private String date;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private List<ExBetMatchDto> matches;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ExBetMatchDto {
		private String tournamentName;
		private LocalDateTime kickoffTime;
		private int homeId;
		private String homeName;
		private int awayId;
		private String awayName;
		private ImportMatchesJsonFile.RoundDto round;
	}
}
