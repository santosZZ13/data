package org.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchDto;

import java.util.List;

public interface SaveMatchesDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private List<ExBetMatchDto> matches;
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
