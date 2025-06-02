package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchDto.RoundDto;

import java.time.LocalDateTime;
import java.util.List;

public interface GetMatchesExByDateDto {
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
		private int id;
		private String tournamentName;
		private LocalDateTime kickoffTime;
		private int homeId;
		private String homeName;
		private int awayId;
		private String awayName;
		private RoundDto round;
		private boolean isFavorite;
	}
}
