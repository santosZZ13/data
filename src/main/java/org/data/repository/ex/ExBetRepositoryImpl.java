package org.data.repository.ex;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.GetMatchesExByDate;
import org.data.dto.ImportMatchesJsonFile;
import org.data.dto.ex.ExCommonDto;
import org.data.dto.ex.ImportExBetFromFile;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.persistent.repository.ExBetMatchMongoRepository;
import org.data.response.ex.ExBetMatchResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.persistent.entity.ExBetEntity;
import org.data.persistent.projection.EventsEightXBetProjection;
import org.data.persistent.repository.ExBetMongoRepository;
import org.data.util.TimeUtil;
import org.data.util.TournamentEightXBetConverter;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
@Log4j2
public class ExBetRepositoryImpl implements ExBetRepository {

	private final ExBetMongoRepository exBetMongoRepository;
	private final ExBetMatchMongoRepository exBetMatchMongoRepository;

	public void saveExBetMatchDto(List<ImportMatchesJsonFile.ExBetMatchDto> exBetMatchDtos) {
		List<ExBetMatchEntity> exBetMatchEntities = exBetMatchDtos.stream().map(
				exBetMatchDto -> ExBetMatchEntity.builder()
						.tournamentName(exBetMatchDto.getTournamentName())
						.homeId(exBetMatchDto.getHomeId())
						.homeName(exBetMatchDto.getHomeName())
						.awayId(exBetMatchDto.getAwayId())
						.awayName(exBetMatchDto.getAwayName())
						.kickoffTime(exBetMatchDto.getKickoffTime())
						.round(ExBetMatchEntity.RoundEntity
								.builder()
								.roundName(exBetMatchDto.getRound().getRoundName())
								.roundType(exBetMatchDto.getRound().getRoundType())
								.build())
						.build()
		).toList();
		exBetMatchMongoRepository.saveAll(exBetMatchEntities);
	}


	@Override
	public List<GetMatchesExByDate.ExBetMatchDto> getExBetByDate(String date) {
		return exBetMatchMongoRepository.findAllByKickoffTimeBetween(
				TimeUtil.convertStringToLocalDateTime(date + " 00:00:00"),
				TimeUtil.convertStringToLocalDateTime(date + " 23:59:59")
			).stream()
				.map(exBetMatchEntity -> GetMatchesExByDate.ExBetMatchDto.builder()
						.tournamentName(exBetMatchEntity.getTournamentName())
						.kickoffTime(exBetMatchEntity.getKickoffTime())
						.homeId(exBetMatchEntity.getHomeId())
						.homeName(exBetMatchEntity.getHomeName())
						.awayId(exBetMatchEntity.getAwayId())
						.awayName(exBetMatchEntity.getAwayName())
						.round(ImportMatchesJsonFile.RoundDto.builder()
								.roundName(exBetMatchEntity.getRound().getRoundName())
								.roundType(exBetMatchEntity.getRound().getRoundType())
								.build())
						.build()).toList();
	}
}
