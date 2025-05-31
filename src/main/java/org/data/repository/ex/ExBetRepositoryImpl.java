package org.data.repository.ex;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.common.SofaMatchDto;
import org.data.dto.ex.GetMatchesExByDateDto;
import org.data.dto.common.ExBetMatchDto;
import org.data.dto.common.ExBetMatchDto.RoundDto;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.persistent.repository.ExBetMatchMongoRepository;
import org.data.repository.sofa.SofaScheduledMatchRepository;
import org.data.util.LevenshteinMatcher;
import org.data.util.NormalizeTeamName;
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
	private final SofaScheduledMatchRepository sofaScheduledMatchRepository;

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

	@Override
	public MatchedMatchesDto getMatchedMatch(ExBetMatchDto matchDto) {
		String normalizedHomeName = NormalizeTeamName.normalize(matchDto.getHomeName());
		String normalizedAwayName = NormalizeTeamName.normalize(matchDto.getAwayName());

		List<SofaMatchDto> candidates = sofaScheduledMatchRepository.findSofaScheduledMatchByName(normalizedHomeName);
		if (candidates == null || candidates.isEmpty()) {
			candidates = sofaScheduledMatchRepository.findSofaScheduledMatchByName(normalizedAwayName);
		}

		if (candidates == null || candidates.isEmpty()) {
			return notFoundMatch(matchDto);
		}

		LocalDateTime kickoffTime = TimeUtil.convertStringToLocalDateTime(matchDto.getKickoffTime());
		assert kickoffTime != null;
		LocalDateTime startTime = kickoffTime.minusMinutes(30);
		LocalDateTime endTime = kickoffTime.plusMinutes(30);

		candidates = candidates.stream()
				.filter(candidate -> {
					LocalDateTime sofaKickoffTime = TimeUtil.convertStringToLocalDateTime(candidate.getStartTimestamp());
					assert sofaKickoffTime != null;
					return sofaKickoffTime.isAfter(startTime) && sofaKickoffTime.isBefore(endTime);
				})
				.collect(Collectors.toList());

		// Nếu không còn ứng viên sau khi lọc thời gian
		if (candidates.isEmpty()) {
			return notFoundMatch(matchDto);
		}

		// Dùng LevenshteinMatcher để chọn best match
		SofaMatchDto bestMatch = null;
		int minDistance = Integer.MAX_VALUE;
		int threshold = 3;

		for (SofaMatchDto sofaMatch : candidates) {
			String sofaHome = (sofaMatch.getHomeNormalizedName());
			String sofaAway = (sofaMatch.getAwayNormalizedName());

			// Kiểm tra đội nhà của 8xbet
			int homeDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaHome);
			int awayDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaAway);

			// Nếu đội nhà 8xbet khớp với đội nhà hoặc đội khách SofaScore
			if (homeDistance <= threshold || awayDistance <= threshold) {
				// Kiểm tra đội còn lại
				String otherTeam = homeDistance <= threshold ? normalizedAwayName : normalizedHomeName;
				String otherSofaTeam = homeDistance <= threshold ? sofaAway : sofaHome;
				int otherDistance = LevenshteinMatcher.calculateLevenshteinDistance(otherTeam, otherSofaTeam);

				if (otherDistance <= threshold && (homeDistance + otherDistance) < minDistance) {
					minDistance = homeDistance + otherDistance;
					bestMatch = sofaMatch;
				}
			}
		}

		if (bestMatch != null) {
			log.info("Matched 8xbet match {} vs {} with SofaScore match {} vs {}",
					matchDto.getHomeName(), matchDto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());
			return MatchedMatchesDto.builder()
					.id(matchDto.getMatchId())
					.homeId(matchDto.getHomeId())
					.homeName(matchDto.getHomeName())
					.awayId(matchDto.getAwayId())
					.awayName(matchDto.getAwayName())
					.kickoffTime(matchDto.getKickoffTime())
					.tournamentName(matchDto.getTournamentName())
					.sofaMatchId(bestMatch.getMatchId())
					.isMatched(true)
					.sofaHomeName(bestMatch.getHomeTeam().getName())
					.sofaAwayName(bestMatch.getAwayTeam().getName())
					.build();
		} else {
			log.info("No SofaScore match found for 8xbet match: {} vs {}", matchDto.getHomeName(), matchDto.getAwayName());
			return notFoundMatch(matchDto);
		}
	}

	private MatchedMatchesDto notFoundMatch(ExBetMatchDto matchDto) {
		log.info("No SofaScore match found within time range for 8xbet match: {} vs {}", matchDto.getHomeName(), matchDto.getAwayName());
		return MatchedMatchesDto.builder()
				.id(matchDto.getMatchId())
				.homeId(matchDto.getHomeId())
				.homeName(matchDto.getHomeName())
				.awayId(matchDto.getAwayId())
				.awayName(matchDto.getAwayName())
				.kickoffTime(matchDto.getKickoffTime())
				.tournamentName(matchDto.getTournamentName())
				.isMatched(false)
				.build();
	}
}
