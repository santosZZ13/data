package org.data.dto.common.lottery;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictedResultDto {
	private int roundId;
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

