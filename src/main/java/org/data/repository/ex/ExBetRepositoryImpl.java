package org.data.repository.ex;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.converter.ExBetMatchConverter;
import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.common.SofaMatchDto;
import org.data.dto.common.ExBetMatchResponseDto;
import org.data.persistent.entity.ExBetMatchEntity;
import org.data.persistent.repository.ExBetMatchMongoRepository;
import org.data.repository.sofa.SofaScheduledMatchRepository;
import org.data.util.LevenshteinMatcher;
import org.data.util.NormalizeTeamName;
import org.data.util.utils.DateUtils;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Log4j2
public class ExBetRepositoryImpl implements ExBetRepository {

	private final ExBetMatchMongoRepository exBetMatchMongoRepository;
	private final SofaScheduledMatchRepository sofaScheduledMatchRepository;
	private final MongoTemplate mongoTemplate;


	public void saveExBetMatchDto(List<ExBetMatchResponseDto> matchesDto) {
		log.info("Starting to save {} matches from EightXBet", matchesDto.size());
		if (matchesDto.isEmpty()) {
			log.warn("No matches to save.");
			return;
		}

		Map<Integer, ExBetMatchResponseDto> uniqueMatchesDto = matchesDto.stream()
				.filter(matchDto -> matchDto.getId() != 0)
				.collect(Collectors.toMap(
						ExBetMatchResponseDto::getId,
						matchDto -> matchDto,
						(existing, replacement) -> existing,
						LinkedHashMap::new
				));

		List<Integer> matchDtoIds = new ArrayList<>(uniqueMatchesDto.keySet());
		if (matchDtoIds.isEmpty()) {
			log.warn("No valid match IDs to process.");
			return;
		}

		Query query = new Query(Criteria.where("matchId").in(matchDtoIds));
		List<ExBetMatchEntity> existingMatchesEntitiesDB = mongoTemplate.find(query, ExBetMatchEntity.class);
		Map<Integer, ExBetMatchEntity> existingMatchEntitiesMap = existingMatchesEntitiesDB.stream()
				.collect(Collectors.toMap(
						ExBetMatchEntity::getMatchId,
						entity -> entity,
						(e1, e2) -> e1,
						LinkedHashMap::new
				));

		List<ExBetMatchEntity> entitiesToSave = new ArrayList<>();

		for (ExBetMatchResponseDto matchDto : uniqueMatchesDto.values()) {
			ExBetMatchEntity entityMatchFromDto = ExBetMatchConverter.toEntity(matchDto);
			ExBetMatchEntity existingEntityMatch = existingMatchEntitiesMap.get(matchDto.getId());
			if (existingEntityMatch == null || !existingEntityMatch.equals(entityMatchFromDto)) {
				if (existingEntityMatch != null) {
					entityMatchFromDto.setId(existingEntityMatch.getId());
				}
				entitiesToSave.add(entityMatchFromDto);
			}
		}

		if (!entitiesToSave.isEmpty()) {
			log.info("Preparing to save or update {} matches.", entitiesToSave.size());
			int batchSize = 500;
			for (int i = 0; i < entitiesToSave.size(); i += batchSize) {
				List<ExBetMatchEntity> batch = entitiesToSave.subList(i, Math.min(i + batchSize, entitiesToSave.size()));
				BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ExBetMatchEntity.class);
				for (ExBetMatchEntity entity : batch) {
					Query upsertQuery = new Query(Criteria.where("matchId").is(entity.getMatchId()));
					Update update = new Update()
							.set("matchId", entity.getMatchId())
							.set("tournamentName", entity.getTournamentName())
							.set("kickoffTime", entity.getKickoffTime())
							.set("homeId", entity.getHomeId())
							.set("homeName", entity.getHomeName())
							.set("awayId", entity.getAwayId())
							.set("awayName", entity.getAwayName())
							.set("isFavorite", entity.isFavorite())
							.set("round", entity.getRound())
							.set("status", entity.getStatus())
							.set("isMatched", entity.getIsMatched())
							.set("sofaDataEntity", entity.getSofaDataEntity());
					bulkOps.upsert(upsertQuery, update);
				}
				bulkOps.execute();
				log.info("Saved batch of {} matches (total processed: {}).", batch.size(), Math.min(i + batchSize, entitiesToSave.size()));
			}
		}

		log.info("Finished saving matches. Total processed: {} at {}", entitiesToSave.size(), new Date());
	}

	@Override
	public List<ExBetMatchResponseDto> getExBetByDate(String date) {
		if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
			log.warn("Invalid date format: {}. Expected YYYY-MM-DD.", date);
			return List.of();
		}
		// Chuyển date thành khoảng thời gian UTC
		ZonedDateTime startOfDay = ZonedDateTime.parse(date + "T00:00:00Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);
		ZonedDateTime endOfDay = ZonedDateTime.parse(date + "T23:59:59Z", DateTimeFormatter.ISO_ZONED_DATE_TIME);

		// Truy vấn MongoDB
		return exBetMatchMongoRepository.findAllByKickoffTimeBetween(startOfDay, endOfDay)
				.stream()
				.map(ExBetMatchConverter::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<ExBetMatchResponseDto> getExBetByDate(String[] date, boolean isFavorite) {
		return null;
//		List<ExBetMatchDto> allMatchesByDate = new ArrayList<>();
//		for (String dt : date) {
//			List<ExBetMatchDto> matches = exBetMatchMongoRepository.findAllByKickoffTimeBetween(
//							TimeUtil.convertStringToLocalDateTimeFormal(dt + " 00:00:00"),
//							TimeUtil.convertStringToLocalDateTimeFormal(dt + " 23:59:59")
//					)
//					.stream()
//					.map(exBetMatchEntity -> ExBetMatchDto.builder()
//							.id(exBetMatchEntity.getMatchId())
//							.tournamentName(exBetMatchEntity.getTournamentName())
//							.kickoffTime(exBetMatchEntity.getKickoffTime())
//							.homeId(exBetMatchEntity.getHomeId())
//							.homeName(exBetMatchEntity.getHomeName())
//							.awayId(exBetMatchEntity.getAwayId())
//							.awayName(exBetMatchEntity.getAwayName())
//							.isFavorite(exBetMatchEntity.isFavorite())
//							.round(RoundDto.builder()
//									.roundName(exBetMatchEntity.getRound().getRoundName())
//									.roundType(exBetMatchEntity.getRound().getRoundType())
//									.build())
//							.build()).toList();
//
//			allMatchesByDate.addAll(matches);
////			ZonedDateTime startTimestamp = DateUtils.toUtcZonedDateTime(dto.getStartTimestamp());
//		}
//		if (isFavorite) {
//			allMatchesByDate = allMatchesByDate.stream()
//					.filter(ExBetMatchDto::isFavorite)
//					.collect(Collectors.toList());
//		}
//		return allMatchesByDate;
	}


	@Override
	public MatchedMatchesDto getMatchedMatch(ExBetMatchResponseDto exBetMatchResponseDto) {
		String normalizedHomeName = NormalizeTeamName.normalize(exBetMatchResponseDto.getHomeName());
		String normalizedAwayName = NormalizeTeamName.normalize(exBetMatchResponseDto.getAwayName());

		List<SofaMatchDto> candidates = sofaScheduledMatchRepository.findSofaScheduledMatchByName(normalizedHomeName);
		if (candidates == null || candidates.isEmpty()) {
			candidates = sofaScheduledMatchRepository.findSofaScheduledMatchByName(normalizedAwayName);
		}

		if (candidates == null || candidates.isEmpty()) {
			return notFoundMatch(exBetMatchResponseDto);
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
					exBetMatchResponseDto.getHomeName(), exBetMatchResponseDto.getAwayName(), bestMatch.getHomeTeam().getName(), bestMatch.getAwayTeam().getName());
			return foundMatch(exBetMatchResponseDto, bestMatch);
		} else {
			log.info("No SofaScore match found for 8xbet match: {} vs {}", exBetMatchResponseDto.getHomeName(), exBetMatchResponseDto.getAwayName());
			return notFoundMatch(exBetMatchResponseDto);
		}
	}

	private MatchedMatchesDto notFoundMatch(ExBetMatchResponseDto matchDto) {
		return MatchedMatchesDto.builder()
				.id(matchDto.getId())
				.homeId(matchDto.getHomeId())
				.homeName(matchDto.getHomeName())
				.awayId(matchDto.getAwayId())
				.awayName(matchDto.getAwayName())
				.kickoffTime(DateUtils.toUtcZonedDateTime(matchDto.getKickoffTime()))
				.tournamentName(matchDto.getTournamentName())
				.isMatched(false)
				.build();
	}

	private MatchedMatchesDto foundMatch(ExBetMatchResponseDto exBetMatchResponseDto, SofaMatchDto sofaMatch) {
		MatchedMatchesDto.SofaData sofaData = MatchedMatchesDto.SofaData.builder()
				.sofaHomeId(sofaMatch.getHomeTeam().getId())
				.sofaAwayId(sofaMatch.getAwayTeam().getId())
				.sofaHomeName(sofaMatch.getHomeTeam().getName())
				.sofaAwayName(sofaMatch.getAwayTeam().getName())
				.sofaMatchId(sofaMatch.getMatchId())
				.build();

		return MatchedMatchesDto.builder()
				.id(exBetMatchResponseDto.getId())
				.homeId(exBetMatchResponseDto.getHomeId())
				.homeName(exBetMatchResponseDto.getHomeName())
				.awayId(exBetMatchResponseDto.getAwayId())
				.awayName(exBetMatchResponseDto.getAwayName())
				.kickoffTime(DateUtils.toUtcZonedDateTime(exBetMatchResponseDto.getKickoffTime()))
				.tournamentName(exBetMatchResponseDto.getTournamentName())
				.isMatched(Boolean.TRUE)
				.sofaData(sofaData)
				.build();
	}

	@Override
	public void saveMatchedMatches(List<MatchedMatchesDto> matchedMatchesDtos) {

	}

	@Override
	public void updateStatusByIds(List<Integer> matchIds, String status) {
		if (matchIds.isEmpty()) {
			log.warn("No match IDs provided for status update.");
			return;
		}

		log.info("Updating status to {} for {} matches.", status, matchIds.size());
		BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ExBetMatchEntity.class);
		for (Integer matchId : matchIds) {
			Query query = new Query(Criteria.where("matchId").is(matchId));
			Update update = new Update().set("status", status);
			bulkOps.updateOne(query, update);
		}
		bulkOps.execute();
		log.info("Updated status for {} matches.", matchIds.size());
	}
}
