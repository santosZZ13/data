package org.data.persistent.repository;

import org.data.persistent.entity.EventsEightXBetEntity;
import org.data.persistent.projection.EventsEightXBetProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledEventsEightXBetMongoRepository extends MongoRepository<EventsEightXBetEntity, String> {
	@Override
	<S extends EventsEightXBetEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);

	@Query(value = "{}", fields = "{iId: 1, _id: 0}")
	List<EventsEightXBetProjection> findAllByIId();

	//  get all events with inplay = true
	@Query(value = "{inplay: true}")
	List<EventsEightXBetEntity> findAllByInPlay();
}