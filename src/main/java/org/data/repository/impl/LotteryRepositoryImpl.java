package org.data.repository.impl;

import lombok.AllArgsConstructor;
import org.data.dto.common.lottery.LotteryResultDto;
import org.data.persistent.entity.LotteryEntity;
import org.data.persistent.repository.LotteryRepositoryMongoRepository;
import org.data.repository.LotteryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class LotteryRepositoryImpl implements LotteryRepository {

	private final LotteryRepositoryMongoRepository lotteryRepositoryMongoRepository;

	@Override
	public void saveLotteryDto(List<LotteryResultDto> lotteriesDto) {
		List<LotteryEntity> entities = lotteriesDto.stream().map(dto -> {
			LotteryEntity entity = LotteryEntity.builder()
					.roundId(dto.getRoundId())
					.lotteryID(dto.getLotteryId())
					.roundTime(dto.getRoundTime())
					.specialPrize(dto.getSpecialPrize())
					.firstPrize(dto.getFirstPrize())
					.secondPrize(dto.getSecondPrize())
					.thirdPrize(dto.getThirdPrize())
					.fourthPrize(dto.getFourthPrize())
					.fifthPrize(dto.getFifthPrize())
					.sixthPrize(dto.getSixthPrize())
					.seventhPrize(dto.getSeventhPrize())
					.created(LocalDateTime.now())
					.updated(LocalDateTime.now())
					.build();
			return entity;
		}).toList();
		if (entities.isEmpty()) {
			return;
		}
		lotteryRepositoryMongoRepository.saveAll(entities);
	}
}
