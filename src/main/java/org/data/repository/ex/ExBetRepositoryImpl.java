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
								.matchId(exBetMatchDto.getId())
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
							.id(exBetMatchEntity.getMatchId())
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
	public MatchedMatchesDto getMatchedMatch(ExBetMatchDto exBetMatchDto) {
		String normalizedHomeName = NormalizeTeamName.normalize(exBetMatchDto.getHomeName());
		String normalizedAwayName = NormalizeTeamName.normalize(exBetMatchDto.getAwayName());

		List<SofaMatchDto> candidates = sofaScheduledMatchRepository.findSofaScheduledMatchByName(normalizedHomeName);
		if (candidates == null || candidates.isEmpty()) {
			candidates = sofaScheduledMatchRepository.findSofaScheduledMatchByName(normalizedAwayName);
		}

		if (candidates == null || candidates.isEmpty()) {
			return notFoundMatch(exBetMatchDto);
		}

//		LocalDateTime kickoffTime = TimeUtil.convertStringToLocalDateTime(matchDto.getKickoffTime());
//		assert kickoffTime != null;
//		LocalDateTime startTime = kickoffTime.minusMinutes(30);
//		LocalDateTime endTime = kickoffTime.plusMinutes(30);
//
//		candidates = candidates.stream()
//				.filter(candidate -> {
//					LocalDateTime sofaKickoffTime = TimeUtil.convertStringToLocalDateTime(candidate.getStartTimestamp());
//					assert sofaKickoffTime != null;
//					return sofaKickoffTime.isAfter(startTime) && sofaKickoffTime.isBefore(endTime);
//				})
//				.collect(Collectors.toList());

		// Nếu không còn ứng viên sau khi lọc thời gian
//		if (candidates.isEmpty()) {
//			return notFoundMatch(matchDto);
//		}

		// Dùng LevenshteinMatcher để chọn best match
		SofaMatchDto bestMatch = null;
		int minDistance = Integer.MAX_VALUE;
		int threshold = 3;

		for (SofaMatchDto sofaMatch : candidates) {
			String sofaHome = sofaMatch.getHomeTeam().getNormalizedName();
			String sofaAway = sofaMatch.getAwayTeam().getNormalizedName();

			int homeDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaHome);
			int awayDistance = LevenshteinMatcher.calculateLevenshteinDistance(normalizedHomeName, sofaAway);

			if (homeDistance <= threshold || awayDistance <= threshold) {
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
			log.info("Found SofaScore match for 8xbet match: {} vs {} with SofaScore match: {} vs {}",
					exBetMatchDto.getHomeName(), exBetMatchDto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());
			return foundMatch(exBetMatchDto, bestMatch);
		} else {
			log.info("No SofaScore match found for 8xbet match: {} vs {}", exBetMatchDto.getHomeName(), exBetMatchDto.getAwayName());
			return notFoundMatch(exBetMatchDto);
		}
	}

	private MatchedMatchesDto notFoundMatch(ExBetMatchDto matchDto) {
		return MatchedMatchesDto.builder()
				.id(matchDto.getId())
				.homeId(matchDto.getHomeId())
				.homeName(matchDto.getHomeName())
				.awayId(matchDto.getAwayId())
				.awayName(matchDto.getAwayName())
				.kickoffTime(matchDto.getKickoffTime())
				.tournamentName(matchDto.getTournamentName())
				.isMatched(false)
				.build();
	}

	private MatchedMatchesDto foundMatch(ExBetMatchDto exBetMatchDto, SofaMatchDto sofaMatch) {
		MatchedMatchesDto.SofaData sofaData = MatchedMatchesDto.SofaData.builder()
				.sofaHomeId(sofaMatch.getHomeTeam().getId())
				.sofaAwayId(sofaMatch.getAwayTeam().getId())
				.sofaHomeName(sofaMatch.getHomeTeam().getName())
				.sofaAwayName(sofaMatch.getAwayTeam().getName())
				.sofaMatchId(sofaMatch.getMatchId())
				.build();

		return MatchedMatchesDto.builder()
				.id(exBetMatchDto.getId())
				.homeId(exBetMatchDto.getHomeId())
				.homeName(exBetMatchDto.getHomeName())
				.awayId(exBetMatchDto.getAwayId())
				.awayName(exBetMatchDto.getAwayName())
				.kickoffTime(exBetMatchDto.getKickoffTime())
				.tournamentName(exBetMatchDto.getTournamentName())
				.isMatched(Boolean.TRUE)
				.sofaData(sofaData)
				.build();
	}

	@Override
	public void saveMatchedMatches(List<MatchedMatchesDto> matchedMatchesDtos) {

	}
}
