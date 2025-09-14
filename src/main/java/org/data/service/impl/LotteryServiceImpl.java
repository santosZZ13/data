package org.data.service.impl;

import lombok.AllArgsConstructor;
import org.data.dto.common.lottery.LotteryDto;
import org.data.dto.common.lottery.LotteryResultDto;
import org.data.dto.common.lottery.PredictedResultDto;
import org.data.dto.lottery.GetLotteryResultPredictDto;
import org.data.dto.lottery.PostLotteryResultPredictDto;
import org.data.dto.lottery.SaveLotteryDto;
import org.data.persistent.entity.PredictionLotteryEntity;
import org.data.persistent.repository.CollectionSeqRepository;
import org.data.persistent.repository.PredictionLotteryRepository;
import org.data.repository.LotteryRepository;
import org.data.service.LotteryService;
import org.data.util.utils.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LotteryServiceImpl implements LotteryService {

	private LotteryRepository lotteryRepository;
	private final CollectionSeqRepository collectionSeqRepository;
	private final PredictionLotteryRepository predictionLotteryRepository;

	/**
	 * Saves lottery data.
	 *
	 * @param request the request containing lottery data to be saved
	 * @return a response indicating the result of the save operation
	 */
	@Override
	public SaveLotteryDto.Response saveLotteries(SaveLotteryDto.Request request) {
		List<LotteryResultDto> lotteriesDto = request.getLotteries();
		if (lotteriesDto == null || lotteriesDto.isEmpty()) {
			return SaveLotteryDto.Response.builder()
					.message("No lotteries to save")
					.totalLotteries(0)
					.build();
		}
		lotteryRepository.saveLotteryDto(lotteriesDto);
		return SaveLotteryDto.Response.builder()
				.message("Lotteries saved successfully")
				.totalLotteries(lotteriesDto.size())
				.build();
	}

	@Override
	public PostLotteryResultPredictDto.Response predictResults(PostLotteryResultPredictDto.Request request) {
		PostLotteryResultPredictDto.LotteryResultData lotteryResultData = request.getData();
		long next = collectionSeqRepository.next("lottery_prediction");

		List<PredictionLotteryEntity.LotteryPredictionEntity> predictionsEntities = new ArrayList<>();

		lotteryResultData.getResults().forEach(
				lotteryResultDto -> {
					LotteryResultDto lotteryResults = lotteryResultDto.getLotteryResult();
					PredictedResultDto predictResults = lotteryResultDto.getPredictedResult();

					PredictionLotteryEntity.LotteryResultEntity lotteryResultEntity = null;
					if (Objects.nonNull(lotteryResults)) {
						lotteryResultEntity = PredictionLotteryEntity.LotteryResultEntity
								.builder()
								.lotteryID(lotteryResults.getLotteryId())
								.roundId(lotteryResults.getRoundId())
								.roundTime(lotteryResults.getRoundTime())
//							.closeTime(resultDto.getCloseTime())
								.specialPrize(lotteryResults.getSpecialPrize())
								.firstPrize(lotteryResults.getFirstPrize())
								.secondPrize(lotteryResults.getSecondPrize())
								.thirdPrize(lotteryResults.getThirdPrize())
								.fourthPrize(lotteryResults.getFourthPrize())
								.fifthPrize(lotteryResults.getFifthPrize())
								.sixthPrize(lotteryResults.getSixthPrize())
								.seventhPrize(lotteryResults.getSeventhPrize())
								.build();

					}

					PredictionLotteryEntity.PredictedResultEntity predictedResultEntity = null;
					if (Objects.nonNull(predictResults)) {
						predictedResultEntity = PredictionLotteryEntity.PredictedResultEntity
								.builder()
								.roundId(predictResults.getRoundId())
								.predict(predictResults.getPredict())
								.result(predictResults.getResult())
								.status(predictResults.getStatus())
								.specialPrize(predictResults.getSpecialPrize())
								.betAmount(predictResults.getBetAmount())
								.profit(predictResults.getProfit())
								.loss(predictResults.getLoss())
								.totalProfit(predictResults.getTotalProfit())
								.build();
					}


					PredictionLotteryEntity.LotteryPredictionEntity predictionEntity = new PredictionLotteryEntity.LotteryPredictionEntity();
					predictionEntity.setLotteryResultEntity(lotteryResultEntity);
					predictionEntity.setPredictedResultEntity(predictedResultEntity);
					predictionsEntities.add(predictionEntity);
				}
		);


		PredictionLotteryEntity entity = PredictionLotteryEntity.builder()
				.phaseId(next)
				.startTime(TimeUtil.convertStringToLocalDateTime(lotteryResultData.getStartTime()))
				.endTime(TimeUtil.convertStringToLocalDateTime(lotteryResultData.getEndTime()))
				.total(lotteryResultData.getTotal())
				.win(lotteryResultData.getWin())
				.lose(lotteryResultData.getLose())
				.initialBalance(lotteryResultData.getInitialBalance())
				.currentBalance(lotteryResultData.getCurrentBalance())
				.results(predictionsEntities)
				.created(LocalDateTime.now())
				.updated(LocalDateTime.now())
				.build();

		predictionLotteryRepository.save(entity);
		return PostLotteryResultPredictDto.Response.builder()
				.message("Prediction results saved successfully")
				.build();
	}


	@Override
	public GetLotteryResultPredictDto.Response predictResults() {
		List<PredictionLotteryEntity> all = predictionLotteryRepository.findAll();
		List<PostLotteryResultPredictDto.LotteryResultData> data = all.stream().map(
				entity -> {
					List<PredictionLotteryEntity.LotteryPredictionEntity> entityResults = entity.getResults();
					List<LotteryDto> lotteryDtos = new ArrayList<>();
					if (Objects.nonNull(entityResults) && !entityResults.isEmpty()) {
						entityResults.forEach(
								result -> {
									LotteryResultDto lotteryResultDto = null;
									if (Objects.nonNull(result.getLotteryResultEntity())) {
										PredictionLotteryEntity.LotteryResultEntity lotteryResultEntity = result.getLotteryResultEntity();
										lotteryResultDto = LotteryResultDto.builder()
												.roundId(lotteryResultEntity.getRoundId())
												.lotteryId(lotteryResultEntity.getLotteryID())
												.roundTime(lotteryResultEntity.getRoundTime())
												.specialPrize(lotteryResultEntity.getSpecialPrize())
												.firstPrize(lotteryResultEntity.getFirstPrize())
												.secondPrize(lotteryResultEntity.getSecondPrize())
												.thirdPrize(lotteryResultEntity.getThirdPrize())
												.fourthPrize(lotteryResultEntity.getFourthPrize())
												.fifthPrize(lotteryResultEntity.getFifthPrize())
												.sixthPrize(lotteryResultEntity.getSixthPrize())
												.seventhPrize(lotteryResultEntity.getSeventhPrize())
												.build();
									}
									PredictedResultDto predictedResultDto = null;
									if (Objects.nonNull(result.getPredictedResultEntity())) {
										PredictionLotteryEntity.PredictedResultEntity predictedResultEntity = result.getPredictedResultEntity();
										predictedResultDto = PredictedResultDto.builder()
												.roundId(predictedResultEntity.getRoundId())
												.predict(predictedResultEntity.getPredict())
												.result(predictedResultEntity.getResult())
												.status(predictedResultEntity.getStatus())
												.specialPrize(predictedResultEntity.getSpecialPrize())
												.betAmount(predictedResultEntity.getBetAmount())
												.profit(predictedResultEntity.getProfit())
												.loss(predictedResultEntity.getLoss())
												.totalProfit(predictedResultEntity.getTotalProfit())
												.build();
									}
									LotteryDto lotteryDto = LotteryDto.builder()
											.lotteryResult(lotteryResultDto)
											.predictedResult(predictedResultDto)
											.build();
									lotteryDtos.add(lotteryDto);
								}
						);
					}
					return PostLotteryResultPredictDto.LotteryResultData.builder()
							.phaseId(entity.getPhaseId())
							.startTime(TimeUtil.convertLocalDateTimeToString(entity.getStartTime()))
							.endTime(TimeUtil.convertLocalDateTimeToString(entity.getEndTime()))
							.total(entity.getTotal())
							.win(entity.getWin())
							.lose(entity.getLose())
							.initialBalance(entity.getInitialBalance())
							.currentBalance(entity.getCurrentBalance())
							.results(lotteryDtos)
							.build();
				}
		).toList();
		return GetLotteryResultPredictDto.Response.builder()
				.message("Prediction results retrieved successfully")
				.data(data)
				.build();
	}
}
