package org.data.repository.sofa;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.converter.sf.SofaMatchConverter;
import org.data.dto.common.SofaMatchDto;
import org.data.persistent.entity.SofaScheduledMatchEntity;
import org.data.persistent.repository.SofaScheduledMatchMongoRepository;
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

		String normalizedName = normalizeTeamName(name);

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
	public SofaMatchDto findSofaScheduledMatchByName(String name) {
		if (name == null || name.trim().isEmpty()) {
			log.warn("Search name is null or empty.");
			return null;
		}

		// Tách chuỗi tìm kiếm thành hai tên đội bóng
		String[] teamNames = splitTeamNames(name);
		if (teamNames.length != 2) {
			log.warn("Search term must contain exactly two team names: {}", name);
			return null;
		}

		String normalizedTeam1 = normalizeTeamName(teamNames[0]);
		String normalizedTeam2 = normalizeTeamName(teamNames[1]);

		if (normalizedTeam1.isEmpty() || normalizedTeam2.isEmpty()) {
			log.warn("Invalid team names after normalization: team1={}, team2={}", normalizedTeam1, normalizedTeam2);
			return null;
		}

		log.info("Searching for match between teams: {} vs {}", normalizedTeam1, normalizedTeam2);

		// Tạo text criteria cho từng đội bóng
		TextCriteria team1CriteriaHome = TextCriteria.forDefaultLanguage()
				.matching(normalizedTeam1);
		TextCriteria team1CriteriaAway = TextCriteria.forDefaultLanguage()
				.matching(normalizedTeam1);
		TextCriteria team2CriteriaHome = TextCriteria.forDefaultLanguage()
				.matching(normalizedTeam2);
		TextCriteria team2CriteriaAway = TextCriteria.forDefaultLanguage()
				.matching(normalizedTeam2);

		// Tạo query để tìm trận đấu có cả hai đội
		Query query = new Query().addCriteria(
				new Criteria().orOperator(
						// Trường hợp: Team1 là homeTeam, Team2 là awayTeam
						new Criteria().andOperator(
								Criteria.where("homeTeam.name").is(team1CriteriaHome),
								Criteria.where("awayTeam.name").is(team2CriteriaAway)
						),
						// Trường hợp: Team1 là awayTeam, Team2 là homeTeam
						new Criteria().andOperator(
								Criteria.where("homeTeam.name").is(team2CriteriaHome),
								Criteria.where("awayTeam.name").is(team1CriteriaAway)
						)
				)
		).limit(1); // Chỉ lấy 1 kết quả phù hợp nhất

		SofaScheduledMatchEntity entity = mongoTemplate.findOne(query, SofaScheduledMatchEntity.class);
		if (entity == null) {
			log.info("No match found for teams: {} vs {}", normalizedTeam1, normalizedTeam2);
			return null;
		}

		log.info("Found match for teams: {} vs {}, matchId: {}", normalizedTeam1, normalizedTeam2, entity.getMatchId());
		return SofaMatchConverter.toDto(entity);
	}


	private String normalizeTeamName(String name) {
		if (name == null) {
			return "";
		}
		// Chuyển về lowercase, loại bỏ ký tự đặc biệt, hậu tố
		return name.toLowerCase()
				.replaceAll("fc|afc|serie a|2025", "") // Loại bỏ hậu tố và năm
				.replaceAll("[^a-z0-9\\s]", "") // Loại bỏ ký tự đặc biệt
				.trim(); // Loại bỏ khoảng trắng thừa
	}

	private String[] splitTeamNames(String searchTerm) {
		// Chuẩn hóa chuỗi tìm kiếm
		String normalized = searchTerm.trim().replaceAll("\\s+", " ");
		String[] words = normalized.split(" ");

		// Nếu chuỗi có ít hơn 2 từ, không thể tách thành hai đội
		if (words.length < 2) {
			return new String[]{};
		}

		// Heuristic đơn giản: Chia chuỗi thành hai phần gần bằng nhau
		int midPoint = words.length / 2;
		String team1 = String.join(" ", Arrays.copyOfRange(words, 0, midPoint));
		String team2 = String.join(" ", Arrays.copyOfRange(words, midPoint, words.length));

		return new String[]{team1, team2};
	}


}
