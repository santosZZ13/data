package org.data.dto.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.LotteryDto;
import org.data.dto.ex.GetAnalystDto;

import java.util.List;

public interface SaveLotteryDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		List<LotteryDto> lotteries;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private String message;
		private int totalLotteries;
	}
}
