package org.data.repository.sofa;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.converter.sf.SofaMatchConverter;
import org.data.dto.common.SofaMatchDto;
import org.data.persistent.entity.SofaScheduledMatchEntity;
import org.data.persistent.repository.SofaScheduledMatchMongoRepository;
import org.data.util.NormalizeTeamName;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Log4j2
public class SofaScheduledMatchRepositoryImpl implements SofaScheduledMatchRepository {
	private final SofaScheduledMatchMongoRepository sofaScheduledMatchMongoRepository;
	private final MongoTemplate mongoTemplate;

	//TODO: Optimize this method to reduce the number of database calls
	@Override
	public void saveSofaScheduledMatches(List<SofaMatchDto> matchesDto) {
		log.info("Starting to save {} matches.", matchesDto.size());
		if (matchesDto.isEmpty()) {
			log.warn("No matches to save.");
			return;
		}

		Map<Integer, SofaMatchDto> uniqueMatchesDto = new LinkedHashMap<>();
		matchesDto.forEach(matchDto -> {
			if (matchDto.getMatchId() != null) {
				uniqueMatchesDto.putIfAbsent(matchDto.getMatchId(), matchDto);
			} else {
				log.warn("Match with null matchId, skipping: {}", matchDto);
			}
		});

		List<Integer> matchDtoIds = new ArrayList<>(uniqueMatchesDto.keySet());
		if (matchDtoIds.isEmpty()) {
			log.warn("No valid match IDs to process.");
			return;
		}
		Query query = new Query(Criteria.where("matchId").in(matchDtoIds));
		List<SofaScheduledMatchEntity> existingMatchesEntitiesDB = mongoTemplate.find(query, SofaScheduledMatchEntity.class);
		Map<Integer, SofaScheduledMatchEntity> existingMatchEntitiesMap = new LinkedHashMap<>();
		for (SofaScheduledMatchEntity entity : existingMatchesEntitiesDB) {
			existingMatchEntitiesMap.put(entity.getMatchId(), entity);
		}

		List<SofaScheduledMatchEntity> entitiesToSave = new ArrayList<>();
		for (SofaMatchDto sofaMatchDto : uniqueMatchesDto.values()) {
			log.debug("Processing match: {}", sofaMatchDto.getMatchId());
			SofaScheduledMatchEntity matchEntityFromDto = SofaMatchConverter.toEntity(sofaMatchDto);
			SofaScheduledMatchEntity existingEntityMatch = existingMatchEntitiesMap.get(sofaMatchDto.getMatchId());

			if (existingEntityMatch == null) {
				log.info("Saving new match with matchId: {}. Details: {}", sofaMatchDto.getMatchId(), matchEntityFromDto);
				entitiesToSave.add(matchEntityFromDto);
			} else {
				if (!existingEntityMatch.equals(matchEntityFromDto)) {
					logTheDifference(matchEntityFromDto, existingEntityMatch);
					log.info("Updating existing match with matchId: {}", sofaMatchDto.getMatchId());
					matchEntityFromDto.setId(existingEntityMatch.getId()); // Giữ ID của bản ghi cũ
					entitiesToSave.add(matchEntityFromDto);
				} else {
					log.debug("No changes for match with matchId: {}, skipping update.", sofaMatchDto.getMatchId());
				}
			}
		}
		if (!entitiesToSave.isEmpty()) {
			// Save all new or updated matches in a single batch operation
			log.info("Saving {} matches to the database.", entitiesToSave.size());
			sofaScheduledMatchMongoRepository.saveAll(entitiesToSave);
		}

		log.info("Finished saving matches. Total saved: {}", entitiesToSave.size());
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
					.append(", country: ").append(entityFromDB.getHomeTeam().getCountry())
					.append("] → [id: ").append(entityFromDto.getHomeTeam().getId())
					.append(", name: ").append(entityFromDto.getHomeTeam().getName())
					.append(", country: ").append(entityFromDto.getHomeTeam().getCountry()).append("], ");
		}

		// So sánh awayTeam
		if (!Objects.equals(entityFromDB.getAwayTeam(), entityFromDto.getAwayTeam())) {
			differences.append("awayTeam: [id: ").append(entityFromDB.getAwayTeam().getId())
					.append(", name: ").append(entityFromDB.getAwayTeam().getName())
					.append(", country: ").append(entityFromDB.getAwayTeam().getCountry())
					.append("] → [id: ").append(entityFromDto.getAwayTeam().getId())
					.append(", name: ").append(entityFromDto.getAwayTeam().getName())
					.append(", country: ").append(entityFromDto.getAwayTeam().getCountry()).append("], ");
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
	public List<SofaMatchDto> findSofaScheduledMatchByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			log.warn("Search name is null or empty.");
			return null;
		}

		String normalizedName = NormalizeTeamName.normalize(name);
		log.info("Searching for matches with normalized team name: {}", normalizedName);
		Query query = new Query().addCriteria(
				new Criteria().orOperator(
						Criteria.where("homeNormalizedName").regex(normalizedName, "i"),
						Criteria.where("awayNormalizedName").regex(normalizedName, "i")
				)
		);
		List<SofaScheduledMatchEntity> sofaScheduledMatchEntities = mongoTemplate.find(query, SofaScheduledMatchEntity.class);
		return sofaScheduledMatchEntities.stream()
				.map(SofaMatchConverter::toDto)
				.collect(Collectors.toList());
	}

}
