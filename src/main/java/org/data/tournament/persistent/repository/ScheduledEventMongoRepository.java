package org.data.tournament.persistent.repository;

import org.data.tournament.persistent.entity.ScheduledEventsEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ScheduledEventMongoRepository extends MongoRepository<ScheduledEventsEntity, String> {
	@Override
	<S extends ScheduledEventsEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
	@Override
	@NotNull
	List<ScheduledEventsEntity> findAll();
	@Override
	<S extends ScheduledEventsEntity> @NotNull S save(@NotNull S entity);
	Page<ScheduledEventsEntity> findAllByStartTimestamp(LocalDateTime startTimestamp, Pageable pageable);
}
