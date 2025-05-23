package org.data.persistent.repository;

import org.data.persistent.entity.ExBetEntity;
import org.data.persistent.entity.ExBetMatchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExBetMatchMongoRepository extends MongoRepository<ExBetMatchEntity, String> {
	@Override
	<S extends ExBetMatchEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
}
