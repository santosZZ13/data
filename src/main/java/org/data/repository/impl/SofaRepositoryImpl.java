package org.data.repository.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.converter.SofaMatchConverter;
import org.data.dto.common.sofa.SofaMatchDto;
import org.data.dto.common.sofa.TeamDto;
import org.data.persistent.entity.SofaScheduledMatchEntity;
import org.data.persistent.repository.SofaScheduledMatchMongoRepository;
import org.data.repository.SofaRepository;
import org.data.repository.TeamRepository;
import org.data.external.sofa.model.SofaMatchResponseDetail;
import org.data.util.NormalizeTeamName;
import org.data.external.sofa.service.SofaApiServiceImpl;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@AllArgsConstructor
@Log4j2
public class SofaRepositoryImpl implements SofaRepository {
	private final SofaScheduledMatchMongoRepository sofaScheduledMatchMongoRepository;
	private final SofaApiServiceImpl sofaApiServiceImpl;
	private final TeamRepository teamRepository;
	private final MongoTemplate mongoTemplate;

	//TODO: Optimize this method to reduce the number of database calls

	/**
	 * @param matchesDto
	 */
	@Override
	public void saveSofaScheduledMatches(List<SofaMatchResponseDetail> matchesDto) {
		log.info("Starting to save {} matches.", matchesDto.size());
		if (matchesDto.isEmpty()) {
			log.warn("No matches to save.");
			return;
		}
//		saveTeamDto(matchesDto);
		Map<Integer, SofaMatchResponseDetail> uniqueMatchesDto = matchesDto.stream()
				.filter(matchDto -> matchDto.getMatchId() != null)
				.collect(Collectors.toMap(
						SofaMatchResponseDetail::getMatchId,
						matchDto -> matchDto,
						(existing, replacement) -> existing, LinkedHashMap::new)
				);

		List<Integer> matchDtoIds = new ArrayList<>(uniqueMatchesDto.keySet());
		if (matchDtoIds.isEmpty()) {
			log.warn("No valid match IDs to process.");
			return;
		}

		Query query = new Query(Criteria.where("matchId").in(matchDtoIds));
		List<SofaScheduledMatchEntity> existingMatchesEntitiesDB = mongoTemplate.find(query, SofaScheduledMatchEntity.class);
		Map<Integer, SofaScheduledMatchEntity> existingMatchEntitiesMap = existingMatchesEntitiesDB.stream()
				.collect(Collectors.toMap(
						SofaScheduledMatchEntity::getMatchId,
						entity -> entity,
						(e1, e2) -> e1, LinkedHashMap::new)
				);

		List<SofaScheduledMatchEntity> entitiesToSave = new ArrayList<>();
		for (SofaMatchResponseDetail sofaMatchDto : uniqueMatchesDto.values()) {
			SofaScheduledMatchEntity matchEntityFromDto = SofaMatchConverter.toEntity(sofaMatchDto);
			SofaScheduledMatchEntity existingEntityMatch = existingMatchEntitiesMap.get(sofaMatchDto.getMatchId());

			if (existingEntityMatch == null || !existingEntityMatch.equals(matchEntityFromDto)) {
				if (existingEntityMatch != null) {
					matchEntityFromDto.setId(existingEntityMatch.getId());
				}
				entitiesToSave.add(matchEntityFromDto);
			}
		}

		// Sử dụng Bulk Write Operations để lưu dữ liệu
		if (!entitiesToSave.isEmpty()) {
			log.info("Preparing to save or update {} matches.", entitiesToSave.size());
			int batchSize = 500;
			for (int i = 0; i < entitiesToSave.size(); i += batchSize) {
				List<SofaScheduledMatchEntity> batch = entitiesToSave.subList(i, Math.min(i + batchSize, entitiesToSave.size()));
				// Sử dụng BulkOperations để upsert
				BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SofaScheduledMatchEntity.class);
				for (SofaScheduledMatchEntity entity : batch) {
					Query upsertQuery = new Query(Criteria.where("matchId").is(entity.getMatchId()));
					Update update = new Update()
							.set("matchId", entity.getMatchId())
							.set("startTimestamp", entity.getStartTimestamp())
							.set("tournamentInfo", entity.getTournamentInfo())
							.set("sessionInfo", entity.getSessionInfo())
							.set("roundInfo", entity.getRoundInfo())
							.set("status", entity.getStatus())
							.set("homeTeam", entity.getHomeTeam())
							.set("awayTeam", entity.getAwayTeam())
							.set("homeScore", entity.getHomeScore())
							.set("awayScore", entity.getAwayScore());
					bulkOps.upsert(upsertQuery, update);
				}
				bulkOps.execute();
				log.info("Saved batch of {} matches (total processed: {}).", batch.size(), Math.min(i + batchSize, entitiesToSave.size()));
			}
		}
		log.info("Finished saving matches. Total processed: {} at {}", entitiesToSave.size(), new Date());
	}

	private void saveTeamDto(List<SofaMatchDto> matchesDto) {
		if (matchesDto.isEmpty()) {
			return;
		}
		log.info("Starting to save {} teams from matches", matchesDto.size() * 2);
		List<TeamDto> teams = matchesDto.stream()
				.flatMap(matchDto -> Stream.of(matchDto.getHomeTeam(), matchDto.getAwayTeam()))
				.filter(Objects::nonNull)
				.distinct()
				.map(team -> {
					TeamDto.SofaTeamDto sofaTeamDto = TeamDto.SofaTeamDto.builder()
							.teamId(team.getId())
							.name(team.getName())
							.country(team.getCountry())
							.normalizedName(NormalizeTeamName.normalize(team.getName()))
							.shortName(team.getShortName())
							.build();
					return TeamDto.builder()
							.sofaTeamDto(sofaTeamDto)
							.exBetTeamDto(null)
							.build();
				})
				.collect(Collectors.toList());

		log.info("Saving {} unique teams.", teams.size());
		teamRepository.saveTeamsFromSofa(teams);
	}


	/**
	 * Logs the differences between the DTO and the entity from the database.
	 * This method is used for debugging purposes to track changes in match details.
	 *
	 * @param entityFromDto The match entity from the DTO.
	 * @param entityFromDB  The match entity from the database.
	 */
	public void logTheDifference(SofaScheduledMatchEntity entityFromDto, SofaScheduledMatchEntity entityFromDB) {
		StringBuilder differences = new StringBuilder();

		// So sánh status
		if (!Objects.equals(entityFromDB.getStatus(), entityFromDto.getStatus())) {
			differences.append("status: [description: ").append(entityFromDB.getStatus().getDescription())
					.append(", type: ").append(entityFromDB.getStatus().getType())
					.append("] → [description: ").append(entityFromDto.getStatus().getDescription())
					.append(", type: ").append(entityFromDto.getStatus().getType()).append("], ");
		}

		// So sánh homeTeam
		if (!Objects.equals(entityFromDB.getHomeTeam(), entityFromDto.getHomeTeam())) {
			differences.append("homeTeam: [id: ").append(entityFromDB.getHomeTeam().getId())
					.append(", name: ").append(entityFromDB.getHomeTeam().getName())
					.append("] → [id: ").append(entityFromDto.getHomeTeam().getId())
					.append(", name: ").append(entityFromDto.getHomeTeam().getName());
		}

		// So sánh awayTeam
		if (!Objects.equals(entityFromDB.getAwayTeam(), entityFromDto.getAwayTeam())) {
			differences.append("awayTeam: [id: ").append(entityFromDB.getAwayTeam().getId())
					.append(", name: ").append(entityFromDB.getAwayTeam().getName())
					.append("] → [id: ").append(entityFromDto.getAwayTeam().getId())
					.append(", name: ").append(entityFromDto.getAwayTeam().getName());
		}

		// So sánh homeScore
		if (!Objects.equals(entityFromDB.getHomeScore(), entityFromDto.getHomeScore())) {
			differences.append("homeScore: [current: ").append(entityFromDB.getHomeScore().getCurrent())
					.append(", display: ").append(entityFromDB.getHomeScore().getDisplay())
					.append(", period1: ").append(entityFromDB.getHomeScore().getPeriod1())
					.append(", period2: ").append(entityFromDB.getHomeScore().getPeriod2())
					.append(", normalTime: ").append(entityFromDB.getHomeScore().getNormalTime())
					.append(", extra1: ").append(entityFromDB.getHomeScore().getExtra1())
					.append(", extra2: ").append(entityFromDB.getHomeScore().getExtra2())
					.append(", overtime: ").append(entityFromDB.getHomeScore().getOvertime())
					.append(", penalties: ").append(entityFromDB.getHomeScore().getPenalties())
					.append(", scoreEmpty: ").append(entityFromDB.getHomeScore().getScoreEmpty())
					.append("] → [current: ").append(entityFromDto.getHomeScore().getCurrent())
					.append(", display: ").append(entityFromDto.getHomeScore().getDisplay())
					.append(", period1: ").append(entityFromDto.getHomeScore().getPeriod1())
					.append(", period2: ").append(entityFromDto.getHomeScore().getPeriod2())
					.append(", normalTime: ").append(entityFromDto.getHomeScore().getNormalTime())
					.append(", extra1: ").append(entityFromDto.getHomeScore().getExtra1())
					.append(", extra2: ").append(entityFromDto.getHomeScore().getExtra2())
					.append(", overtime: ").append(entityFromDto.getHomeScore().getOvertime())
					.append(", penalties: ").append(entityFromDto.getHomeScore().getPenalties())
					.append(", scoreEmpty: ").append(entityFromDto.getHomeScore().getScoreEmpty())
					.append("], ");
		}

		// So sánh awayScore
		if (!Objects.equals(entityFromDB.getAwayScore(), entityFromDto.getAwayScore())) {
			differences.append("awayScore: [current: ").append(entityFromDB.getAwayScore().getCurrent())
					.append(", display: ").append(entityFromDB.getAwayScore().getDisplay())
					.append(", period1: ").append(entityFromDB.getAwayScore().getPeriod1())
					.append(", period2: ").append(entityFromDB.getAwayScore().getPeriod2())
					.append(", normalTime: ").append(entityFromDB.getAwayScore().getNormalTime())
					.append(", extra1: ").append(entityFromDB.getAwayScore().getExtra1())
					.append(", extra2: ").append(entityFromDB.getAwayScore().getExtra2())
					.append(", overtime: ").append(entityFromDB.getAwayScore().getOvertime())
					.append(", penalties: ").append(entityFromDB.getAwayScore().getPenalties())
					.append(", scoreEmpty: ").append(entityFromDB.getAwayScore().getScoreEmpty())
					.append("] → [current: ").append(entityFromDto.getAwayScore().getCurrent())
					.append(", display: ").append(entityFromDto.getAwayScore().getDisplay())
					.append(", period1: ").append(entityFromDto.getAwayScore().getPeriod1())
					.append(", period2: ").append(entityFromDto.getAwayScore().getPeriod2())
					.append(", normalTime: ").append(entityFromDto.getAwayScore().getNormalTime())
					.append(", extra1: ").append(entityFromDto.getAwayScore().getExtra1())
					.append(", extra2: ").append(entityFromDto.getAwayScore().getExtra2())
					.append(", overtime: ").append(entityFromDto.getAwayScore().getOvertime())
					.append(", penalties: ").append(entityFromDto.getAwayScore().getPenalties())
					.append(", scoreEmpty: ").append(entityFromDto.getAwayScore().getScoreEmpty())
					.append("], ");
		}

		if (!differences.isEmpty()) {
			differences.setLength(differences.length() - 2); // Loại bỏ dấu ", " cuối cùng
			log.debug("Updating match with matchId: {}. Differences: {}", entityFromDto.getMatchId(), differences.toString());
		} else {
			log.debug("No significant changes detected for matchId: {}, but equals method returned false.", entityFromDto.getMatchId());
		}
	}


	/**
	 * * Find the best matching match for the given team names.
	 * * Expects the search term to contain two team names (e.g., "America de Cali Racing Club Montevideo").
	 * * Returns a single match where both teams are present, or null if no match is found.
	 *
	 * @param name
	 * @return
	 */
	@Override
	public List<SofaMatchDto> findSofaScheduledMatchesByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			log.warn("Search name is null or empty.");
			return null;
		}

		String normalizedName = NormalizeTeamName.normalize(name);

		TextCriteria textCriteria = TextCriteria.forDefaultLanguage()
				.matching(normalizedName);

		Query query = TextQuery.queryText(textCriteria)
				.sortByScore(); // Sắp xếp theo độ tương đồng
		List<SofaScheduledMatchEntity> entities = mongoTemplate.find(query, SofaScheduledMatchEntity.class);
		log.info("Found {} matches for team name: {}", entities.size(), name);
		return entities.stream()
				.map(SofaMatchConverter::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<SofaMatchDto> findSofaMatchByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			log.warn("Search name is null or empty.");
			return null;
		}

		String normalizedName = NormalizeTeamName.normalize(name);
		log.info("Searching for matches with normalized team name: {}", normalizedName);
		Query query = new Query().addCriteria(
				new Criteria().orOperator(
						Criteria.where("homeTeam.normalizedName").regex(normalizedName, "i"),
						Criteria.where("awayTeam.normalizedName").regex(normalizedName, "i")
				)
		);
		List<SofaScheduledMatchEntity> sofaScheduledMatchEntities = mongoTemplate.find(query, SofaScheduledMatchEntity.class);
		return sofaScheduledMatchEntities.stream()
				.map(SofaMatchConverter::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<SofaMatchResponseDetail> getMatchesByDate(String date) {
		List<SofaMatchResponseDetail> sofaMatchByDate = sofaApiServiceImpl.getMatchesByDate(date);
		saveSofaScheduledMatches(sofaMatchByDate);
		return sofaMatchByDate;
	}

}
