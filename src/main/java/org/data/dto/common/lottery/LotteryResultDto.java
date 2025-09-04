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
	private int roundId;
	private int lotteryID;
	private String roundTime;
	private String closeTime;
	private int specialPrize;
	private int firstPrize;
	private List<Integer> secondPrize;
	private List<Integer> thirdPrize;
	private List<Integer> fourthPrize;
	private List<Integer> fifthPrize;
	private List<Integer> sixthPrize;
	private List<Integer> seventhPrize;
}
