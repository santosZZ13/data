package org.data.dto.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface GetLotteryResultPredictDto {

//	@Builder
//	@Data
//	@AllArgsConstructor
//	@NoArgsConstructor
//	class Request {
//		private int lotteryId;
//		private int total;
//	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private String message;
		private List<PostLotteryResultPredictDto.LotteryResultData> data;
	}
}
