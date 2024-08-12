package org.data.persistent.repository;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HistoryFetchEventRepository extends MongoRepository<HistoryFetchEventEntity, String> {
	Optional<HistoryFetchEventEntity> findByTeamId(Integer idTeam);

	@Query("{ 'fetchStatus' : ?0 }")
	List<HistoryFetchEventEntity> findHistoryFetchEventEntitiesWithStatus(String fetchStatus);
}
