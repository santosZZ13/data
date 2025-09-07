package org.data.dto.common.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotteryResultDto {
	private long roundId;
	private int lotteryId;
	private long roundTime;
//	private long closeTime;
	private String specialPrize;
	private String firstPrize;
	private List<String> secondPrize;
	private List<String> thirdPrize;
	private List<String> fourthPrize;
	private List<String> fifthPrize;
	private List<String> sixthPrize;
	private List<String> seventhPrize;
}
