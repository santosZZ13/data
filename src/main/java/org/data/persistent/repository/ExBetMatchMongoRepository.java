package org.data.persistent.repository;

import org.data.persistent.entity.ExBetMatchEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExBetMatchMongoRepository extends MongoRepository<ExBetMatchEntity, String> {
	@Override
	<S extends ExBetMatchEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
	List<ExBetMatchEntity> findAllByKickoffTimeBetween(LocalDateTime start, LocalDateTime end);
	List<ExBetMatchEntity> findAllByKickoffTimeBetweenAndFavoriteIsTrue(LocalDateTime start, LocalDateTime end);
	List<ExBetMatchEntity> findAllByKickoffTimeBetweenAndFavoriteIsFalse(LocalDateTime start, LocalDateTime end);
}
