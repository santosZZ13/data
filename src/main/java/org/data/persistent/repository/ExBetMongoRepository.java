package org.data.persistent.repository;

import org.data.persistent.entity.ExBetEntity;
import org.data.persistent.projection.EventsEightXBetProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExBetMongoRepository extends MongoRepository<ExBetEntity, String> {
	@Override
	<S extends ExBetEntity> @NotNull List<S> saveAll(@NotNull Iterable<S> entities);

	@Query(value = "{}", fields = "{iId: 1, _id: 0}")
	List<EventsEightXBetProjection> findAllByIId();

	//  get all events with inplay = true
	@Query(value = "{inplay: true}")
	List<ExBetEntity> findAllByInPlay();


	List<ExBetEntity> findAllByKickoffTime(LocalDateTime date);

	@Query(value = "{kickoffTime: {$gte: ?0, $lt: ?1}}")
	List<ExBetEntity> findAllByKickoffTime(LocalDateTime startOfDay, LocalDateTime endOfDay);
}