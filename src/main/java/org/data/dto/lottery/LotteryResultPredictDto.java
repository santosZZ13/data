package org.data.dto.lottery;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.data.dto.common.lottery.LotteryDto;

import java.time.ZonedDateTime;
import java.util.List;

public interface LotteryResultPredictDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private LotteryResultDataRequest data;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class LotteryResultDataRequest {
//		private int phaseId;
		private ZonedDateTime startTime;
		private ZonedDateTime endTime;
		private int total;
		private int win;
		private int lose;
		private Double initialBalance;
		private Double currentBalance;
		private List<LotteryDto> results;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private String message;
	}
}
