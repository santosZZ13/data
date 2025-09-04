package org.data.dto.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.ex.ExBetMatchDto;
import org.data.dto.common.ex.ExBetMatchRequestDto;

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
		private int total;
		private int totalMatched;
		private int totalUnmatched;
		private int totalSaved;
		private int totalUpdated;
		private List<ExBetMatchDto> matches;
	}
}
