package org.data.persistent.repository;

import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledEventMongoRepository extends MongoRepository<ScheduledEventsSofaScoreEntity, String> {
	@Override
	<S extends ScheduledEventsSofaScoreEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
	@Override
	@NotNull
	List<ScheduledEventsSofaScoreEntity> findAll();
	@Override
	<S extends ScheduledEventsSofaScoreEntity> @NotNull S save(@NotNull S entity);
//	Page<ScheduledEventsSofaScoreEntity> findAllByStartTimestamp(LocalDateTime startTimestamp, Pageable pageable);
}
