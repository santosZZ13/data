package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchRequestDto;
import org.data.dto.common.ExBetMatchResponseDto;

import java.util.List;

public interface SaveExBetMatchDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		List<ExBetMatchRequestDto> matches;
	}
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private List<ExBetMatchResponseDto> matches;
	}
}
