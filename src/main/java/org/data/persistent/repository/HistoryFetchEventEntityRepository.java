package org.data.persistent.repository;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HistoryFetchEventEntityRepository extends MongoRepository<HistoryFetchEventEntity, String> {
	Optional<HistoryFetchEventEntity> findByIdTeam(Integer idTeam);
}
