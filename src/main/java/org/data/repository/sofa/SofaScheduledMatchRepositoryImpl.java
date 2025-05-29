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
		if (matchesDto == null || matchesDto.isEmpty()) {
			log.warn("No matches to save.");
			return;
		}

		List<Integer> matchIds = matchesDto.stream()
				.map(SofaMatchDto::getMatchId)
				.filter(Objects::nonNull)
				.toList();

		if (matchIds.isEmpty()) {
			log.warn("No valid match IDs to process.");
			return;
		}

		Query query = new Query(Criteria.where("matchId").in(matchIds));
		List<SofaScheduledMatchEntity> existingMatches = mongoTemplate.find(query, SofaScheduledMatchEntity.class);
		List<SofaScheduledMatchEntity> entitiesToSave = new ArrayList<>();
		Map<Integer, SofaScheduledMatchEntity> existingMatchMap = existingMatches.stream()
				.collect(Collectors.toMap(SofaScheduledMatchEntity::getMatchId, entity -> entity));
		for (SofaMatchDto sofaMatchDto : matchesDto) {
			if (sofaMatchDto.getMatchId() == null) {
				log.warn("Match with null matchId, skipping: {}", sofaMatchDto);
				continue;
			}

			log.debug("Processing match: {}", sofaMatchDto.getMatchId());
			SofaScheduledMatchEntity matchFromDto = SofaMatchConverter.toEntity(sofaMatchDto);
			SofaScheduledMatchEntity existingMatch = existingMatchMap.get(sofaMatchDto.getMatchId());

			if (existingMatch == null) {
				log.debug("Saving new match with matchId: {}", sofaMatchDto.getMatchId());
				entitiesToSave.add(matchFromDto);
			} else {
				if (!existingMatch.equals(matchFromDto)) {
					log.debug("Updating match with matchId: {}", sofaMatchDto.getMatchId());
					matchFromDto.setId(existingMatch.getId()); // Giữ ID của bản ghi cũ
					entitiesToSave.add(matchFromDto);
				} else {
					log.debug("No changes for match with matchId: {}, skipping update.", sofaMatchDto.getMatchId());
				}
			}
		}

		if (!entitiesToSave.isEmpty()) {
			log.info("Saving {} matches to DB", entitiesToSave.size());
			sofaScheduledMatchMongoRepository.saveAll(entitiesToSave);
		}
	}


	/**
	 * 1. Use fuzzy search
	 * 2. Use regex
	 * 3. Text search
	 * })
	 * <p>
	 * db.sofa_scheduled_matches.createIndex(
	 * {
	 * "homeTeam.name": "text",
	 * "awayTeam.name": "text"
	 * },
	 * {
	 * name: "team_name_text_index",
	 * weights: {
	 * "homeTeam.name": 10,
	 * "awayTeam.name": 10
	 * }
	 * }
	 * );
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
