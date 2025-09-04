package org.data.service.impl;

import lombok.AllArgsConstructor;
import org.data.dto.common.lottery.LotteryResultDto;
import org.data.dto.common.lottery.PredictedResultDto;
import org.data.dto.lottery.LotteryResultPredictDto;
import org.data.dto.lottery.SaveLotteryDto;
import org.data.persistent.entity.PredictionLotteryEntity;
import org.data.persistent.repository.CollectionSeqRepository;
import org.data.persistent.repository.PredictionLotteryRepository;
import org.data.repository.LotteryRepository;
import org.data.service.LotteryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
	public LotteryResultPredictDto.Response predictResults(LotteryResultPredictDto.Request request) {
		LotteryResultPredictDto.LotteryResultDataRequest lotteryResultDataRequest = request.getData();
		long next = collectionSeqRepository.next("lottery_prediction");

		List<PredictionLotteryEntity.LotteryPredictionEntity> predictionsEntities = new ArrayList<>();

		lotteryResultDataRequest.getResults().forEach(
				lotteryResultDto -> {
					LotteryResultDto resultDto = lotteryResultDto.getLotteryResultDto();
					PredictedResultDto lotteryResultDto1 = lotteryResultDto.getPredictedResultDto();

					PredictionLotteryEntity.LotteryResultEntity lotteryResultEntity = new PredictionLotteryEntity.LotteryResultEntity();
					PredictionLotteryEntity.PredictedResultEntity predictedResultEntity = new PredictionLotteryEntity.PredictedResultEntity();

					PredictionLotteryEntity.LotteryPredictionEntity predictionEntity = new PredictionLotteryEntity.LotteryPredictionEntity();
					predictionEntity.setLotteryResultEntity(lotteryResultEntity);
					predictionEntity.setPredictedResultEntity(predictedResultEntity);
					predictionsEntities.add(predictionEntity);
				}
		);




		PredictionLotteryEntity entity = PredictionLotteryEntity.builder()
				.phaseId(next)
				.startTime(lotteryResultDataRequest.getStartTime())
				.endTime(lotteryResultDataRequest.getEndTime())
				.total(lotteryResultDataRequest.getTotal())
				.win(lotteryResultDataRequest.getWin())
				.lose(lotteryResultDataRequest.getLose())
				.initialBalance(lotteryResultDataRequest.getInitialBalance())
				.currentBalance(lotteryResultDataRequest.getCurrentBalance())
				.results(predictionsEntities)
				.build();

		predictionLotteryRepository.save(entity);
		return LotteryResultPredictDto.Response.builder()
				.message("Prediction results saved successfully")
				.build();
	}
}
