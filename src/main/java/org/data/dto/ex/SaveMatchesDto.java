package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchResponseDto;

import java.util.List;

public interface SaveMatchesDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private List<ExBetMatchResponseDto> matches;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private String message;
		private int totalMatches;
	}
}
