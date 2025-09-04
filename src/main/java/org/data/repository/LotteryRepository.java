package org.data.repository;

import org.data.dto.common.lottery.LotteryResultDto;


import java.util.List;


public interface LotteryRepository {
	void saveLotteryDto(List<LotteryResultDto> lotteriesDto);
}
