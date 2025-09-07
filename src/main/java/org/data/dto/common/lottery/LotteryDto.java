package org.data.dto.common.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotteryDto {
	private LotteryResultDto lotteryResult;
	private PredictedResultDto predictedResult;
}
