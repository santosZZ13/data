package org.data.service.impl;

import lombok.AllArgsConstructor;
import org.data.dto.common.LotteryDto;
import org.data.dto.lottery.SaveLotteryDto;
import org.data.persistent.entity.LotteryEntity;
import org.data.repository.LotteryRepository;
import org.data.service.LotteryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LotteryServiceImpl implements LotteryService {

	private LotteryRepository lotteryRepository;

	/**
	 * Saves lottery data.
	 *
	 * @param request the request containing lottery data to be saved
	 * @return a response indicating the result of the save operation
	 */
	@Override
	public SaveLotteryDto.Response saveLotteries(SaveLotteryDto.Request request) {
		List<LotteryDto> lotteriesDto = request.getLotteries();
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
}
