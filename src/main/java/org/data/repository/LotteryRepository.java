package org.data.repository;

import org.data.dto.common.LotteryDto;


import java.util.List;


public interface LotteryRepository {
	void saveLotteryDto(List<LotteryDto> lotteriesDto);
}
