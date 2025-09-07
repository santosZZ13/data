package org.data.persistent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Document(collection = "lottery_prediction")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionLotteryEntity {
	private String id;
	private long phaseId;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int total;
	private int win;
	private int lose;
	private Double initialBalance;
	private Double currentBalance;
	private List<LotteryPredictionEntity> results;
	private LocalDateTime created;
	private LocalDateTime updated;


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class LotteryPredictionEntity {
		private LotteryResultEntity lotteryResultEntity;
		private PredictedResultEntity predictedResultEntity;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class LotteryResultEntity {
		private long roundId;
		private int lotteryID;
		private long roundTime;
//		private long closeTime;
		private String specialPrize;
		private String firstPrize;
		private List<String> secondPrize;
		private List<String> thirdPrize;
		private List<String> fourthPrize;
		private List<String> fifthPrize;
		private List<String> sixthPrize;
		private List<String> seventhPrize;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PredictedResultEntity {
		private long roundId;
		private String predict;
		private String result;
		private String status;
		private String specialPrize;

		private Double betAmount;
		private Double initialBalance;
		private Double currentBalance;
		private Double profit;
		private Double loss;
		private Double totalProfit;
	}

}
