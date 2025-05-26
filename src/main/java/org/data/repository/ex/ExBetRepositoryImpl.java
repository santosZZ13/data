package org.data.repository.ex;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.GetMatchesExByDateDto;
import org.data.dto.ImportMatchesJsonFile;
import org.data.dto.common.ExBetMatchDto;
import org.data.dto.common.ExBetMatchDto.RoundDto;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.persistent.repository.ExBetMatchMongoRepository;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Log4j2
public class ExBetRepositoryImpl implements ExBetRepository {

	private final ExBetMatchMongoRepository exBetMatchMongoRepository;

	public int saveExBetMatchDto(List<ExBetMatchDto> matchesDto) {
		List<ExBetMatchEntity> exBetMatchEntities = matchesDto.stream().map(
						exBetMatchDto -> ExBetMatchEntity.builder()
								.matchId(exBetMatchDto.getMatchId())
								.tournamentName(exBetMatchDto.getTournamentName())
								.homeId(exBetMatchDto.getHomeId())
								.homeName(exBetMatchDto.getHomeName())
								.awayId(exBetMatchDto.getAwayId())
								.awayName(exBetMatchDto.getAwayName())
								.kickoffTime(TimeUtil.convertStringToLocalDateTime(exBetMatchDto.getKickoffTime()))
								.isFavorite(exBetMatchDto.isFavorite())
								.round(ExBetMatchEntity.RoundEntity
										.builder()
										.roundName(exBetMatchDto.getRound().getRoundName())
										.roundType(exBetMatchDto.getRound().getRoundType())
										.build())
								.build()
				)
				.collect(Collectors.toList());

		List<ExBetMatchEntity> exBetMatchEntitiesDB = exBetMatchMongoRepository.findAll();
		List<ExBetMatchEntity> exBetMatchEntitiesToSave = new ArrayList<>();
		for (ExBetMatchEntity exBetMatchEntity : exBetMatchEntities) {
			if (exBetMatchEntitiesDB.stream().noneMatch(
					exBetMatchEntityDB -> exBetMatchEntityDB.getMatchId() == exBetMatchEntity.getMatchId())) {
				exBetMatchEntitiesToSave.add(exBetMatchEntity);
			}
		}
		exBetMatchMongoRepository.saveAll(exBetMatchEntitiesToSave);

		return exBetMatchEntitiesToSave.size();
	}


	@Override
	public List<GetMatchesExByDateDto.ExBetMatchDto> getExBetByDate(String[] date, boolean isFavorite) {
		List<GetMatchesExByDateDto.ExBetMatchDto> allMatchesByDate = new ArrayList<>();
		for (String dt : date) {
			List<GetMatchesExByDateDto.ExBetMatchDto> matches = exBetMatchMongoRepository.findAllByKickoffTimeBetween(
							TimeUtil.convertStringToLocalDateTimeFormal(dt + " 00:00:00"),
							TimeUtil.convertStringToLocalDateTimeFormal(dt + " 23:59:59")
					)
					.stream()
					.map(exBetMatchEntity -> GetMatchesExByDateDto.ExBetMatchDto.builder()
							.tournamentName(exBetMatchEntity.getTournamentName())
							.kickoffTime(exBetMatchEntity.getKickoffTime())
							.homeId(exBetMatchEntity.getHomeId())
							.homeName(exBetMatchEntity.getHomeName())
							.awayId(exBetMatchEntity.getAwayId())
							.awayName(exBetMatchEntity.getAwayName())
							.isFavorite(exBetMatchEntity.isFavorite())
							.round(RoundDto.builder()
									.roundName(exBetMatchEntity.getRound().getRoundName())
									.roundType(exBetMatchEntity.getRound().getRoundType())
									.build())
							.build()).toList();

			allMatchesByDate.addAll(matches);
		}
		if (isFavorite) {
			allMatchesByDate = allMatchesByDate.stream()
					.filter(GetMatchesExByDateDto.ExBetMatchDto::isFavorite)
					.collect(Collectors.toList());
		}
		return allMatchesByDate;
	}
}
