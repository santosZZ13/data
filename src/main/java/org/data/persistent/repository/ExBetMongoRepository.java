package org.data.persistent.repository;

import org.data.persistent.entity.ExBetMatchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface ExBetMongoRepository extends MongoRepository<ExBetMatchEntity, String> {
	@Override
	<S extends ExBetMatchEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);

	List<ExBetMatchEntity> findAllByKickoffTimeBetween(ZonedDateTime start, ZonedDateTime end);
}
