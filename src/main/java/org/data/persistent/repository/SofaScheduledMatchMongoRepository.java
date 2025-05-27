package org.data.persistent.repository;

import org.data.persistent.entity.SofaScheduledMatchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SofaScheduledMatchMongoRepository extends MongoRepository<SofaScheduledMatchEntity, String> {
	@Override
	<S extends SofaScheduledMatchEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);

	Optional<SofaScheduledMatchEntity> getByMatchId(Integer matchId);

}
