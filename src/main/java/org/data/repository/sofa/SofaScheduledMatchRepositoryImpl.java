package org.data.repository.sofa;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.converter.sf.SofaMatchConverter;
import org.data.dto.common.SofaMatchDto;
import org.data.persistent.entity.SofaScheduledMatchEntity;
import org.data.persistent.repository.SofaScheduledMatchMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@AllArgsConstructor
@Log4j2
public class SofaScheduledMatchRepositoryImpl implements SofaScheduledMatchRepository {
	private final SofaScheduledMatchMongoRepository sofaScheduledMatchMongoRepository;


	//TODO: Optimize this method to reduce the number of database calls
	@Override
	public void saveSofaScheduledMatches(List<SofaMatchDto> matchesDto) {
		List<SofaScheduledMatchEntity> entitiesToSave = new ArrayList<>();
		// calculate the time running time
		int timeRunning = (int) (System.currentTimeMillis() / 1000);

		if (matchesDto.isEmpty()) {
			return;
		}

		for (SofaMatchDto sofaMatchDto : matchesDto) {
			log.info("Processing match: {}", sofaMatchDto.getMatchId());
			Optional<SofaScheduledMatchEntity> byMatchIdEntity = sofaScheduledMatchMongoRepository.getByMatchId(sofaMatchDto.getMatchId());
			if (byMatchIdEntity.isEmpty()) {
				SofaScheduledMatchEntity sofaScheduledMatchEntity = SofaMatchConverter.toEntity(sofaMatchDto);
				entitiesToSave.add(sofaScheduledMatchEntity);
			} else {
				SofaScheduledMatchEntity matchFromDto = SofaMatchConverter.toEntity(sofaMatchDto);
				SofaScheduledMatchEntity existingMatch = byMatchIdEntity.get();
				if (!existingMatch.equals(matchFromDto)) {
					matchFromDto.setId(existingMatch.getId());
					entitiesToSave.add(matchFromDto);
				}
			}
		}
		// 168 seconds
		int timeRunningAfter = (int) (System.currentTimeMillis() / 1000);
		log.info("Time running for saving matches: {} seconds", timeRunningAfter - timeRunning);

		if (!entitiesToSave.isEmpty()) {
			sofaScheduledMatchMongoRepository.saveAll(entitiesToSave);
		}
		int timeRunningFinal = (int) (System.currentTimeMillis() / 1000);
		log.info("Total time running for saving matches: {} seconds", timeRunningFinal - timeRunning);
	}

	@Override
	public SofaMatchDto findSofaScheduledMatchByName(String name) {
		return null;
	}
}
