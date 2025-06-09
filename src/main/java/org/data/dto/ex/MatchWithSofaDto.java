package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchResponseDto;
import org.data.dto.common.MatchedMatchesDto;

import java.util.List;

public interface MatchWithSofaDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		List<ExBetMatchResponseDto> matches;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		List<MatchedMatchesDto> matches;
	}
}
