package org.data.persistent.repository;

import org.data.persistent.entity.ScheduledEventsEightXBetEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledEventsEightXBetMongoRepository extends MongoRepository<ScheduledEventsEightXBetEntity, String> {
	@Override
	<S extends ScheduledEventsEightXBetEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);
}
