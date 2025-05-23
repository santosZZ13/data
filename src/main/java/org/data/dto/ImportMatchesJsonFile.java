package org.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface ImportMatchesJsonFile {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private MultipartFile file;
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
		private RoundDto round;
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
