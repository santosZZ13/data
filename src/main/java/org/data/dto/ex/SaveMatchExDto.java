package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ExBetMatchDto;
import org.data.dto.common.ExBetMatchResponseDto;

import java.util.List;

public interface SaveMatchExDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		List<ExBetMatchDto> matches;
	}
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private List<ExBetMatchResponseDto> matches;
	}
}
