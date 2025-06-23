package org.data.persistent.repository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.persistent.entity.ExBetMatchEntity;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Log4j2
public class ExBetCustomRepositoryImpl implements ExBetCustomRepository {
	private final MongoTemplate mongoTemplate;
	private static final int BATCH_SIZE = 500;

	@Override
	public List<ExBetMatchEntity> findExBetMatchEntitiesByIds(List<Integer> ids) {
		Query query = new Query(Criteria.where("matchId").in(ids));
		return mongoTemplate.find(query, ExBetMatchEntity.class);
	}

	@Override
	public Map<Integer, ExBetMatchEntity> getEntitiesMap(List<Integer> ids) {
		List<ExBetMatchEntity> exBetMatchEntitiesByIds = findExBetMatchEntitiesByIds(ids);
		return exBetMatchEntitiesByIds.stream()
				.collect(Collectors.toMap(
						ExBetMatchEntity::getMatchId,
						entity -> entity,
						(e1, e2) -> e1, LinkedHashMap::new
				));
	}

	@Override
	public void saveAll(List<ExBetMatchEntity> entities) {
		log.info("Preparing to save or update {} matches.", entities.size());
		for (int i = 0; i < entities.size(); i += BATCH_SIZE) {
			List<ExBetMatchEntity> batch = entities.subList(i, Math.min(i + BATCH_SIZE, entities.size()));
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
			log.info("Saved batch of {} matches (total processed: {}).", batch.size(), Math.min(i + BATCH_SIZE, entities.size()));
		}
	}
}
