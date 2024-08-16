package org.data.persistent.repository;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryFetchEventMongoRepository extends MongoRepository<HistoryFetchEventEntity, String> {
	Optional<HistoryFetchEventEntity> findByTeamId(Integer idTeam);

	@Query("{ 'fetchStatus' : ?0 }")
	List<HistoryFetchEventEntity> findHistoryFetchEventEntitiesWithStatus(String fetchStatus);

	@Query("{ 'fetchStatus' : ?0, 'createdDate' : { $gte: ?1, $lt: ?2 } }")
	Page<HistoryFetchEventEntity> findByFetchStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(String fetchStatus,
																											 LocalDateTime fromDate, LocalDateTime to,
																											 Pageable pageable);
}
