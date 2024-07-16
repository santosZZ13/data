package org.data.tournament.repository.impl;

import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledEventsRepository {
	List<ScheduledEventsSofaScoreEntity> saveEvents(List<ScheduledEventsResponse.Event> events);

	List<ScheduledEventsResponse.Event> getAllEvents();

	ScheduledEventsResponse.Event saveEvent(ScheduledEventsResponse.Event event);

	ScheduledEventsResponse.Event getAllEventByDate(LocalDateTime date, Pageable pageable);
}
