package org.data.service;

import org.data.dto.lottery.GetLotteryResultPredictDto;
import org.data.dto.lottery.PostLotteryResultPredictDto;
import org.data.dto.lottery.SaveLotteryDto;

public interface LotteryService {
	/**
	 * Saves lottery data.
	 *
	 * @param request the request containing lottery data to be saved
	 * @return a response indicating the result of the save operation
	 */
	SaveLotteryDto.Response saveLotteries(SaveLotteryDto.Request request);

	PostLotteryResultPredictDto.Response predictResults(PostLotteryResultPredictDto.Request request);
	GetLotteryResultPredictDto.Response predictResults();

//	/**
//	 * Finds lotteries by name.
//	 *
//	 * @param name the name of the lottery to search for
//	 * @return a response containing the lotteries found
//	 */
//	GetLotteriesByName.Response findLotteriesByName(String name);

//	GetLotteryByName.Response findLotteryByName(String name);
}


