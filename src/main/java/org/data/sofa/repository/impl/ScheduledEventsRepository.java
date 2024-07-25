package org.data.sofa.repository.impl;

import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.data.sofa.dto.SofaScheduledEventsResponse.EventResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledEventsRepository {
	List<ScheduledEventsSofaScoreEntity> saveEvents(List<EventResponse> eventResponses);

	List<EventResponse> getAllEvents();

	EventResponse saveEvent(EventResponse eventResponse);

	EventResponse getAllEventByDate(LocalDateTime date, Pageable pageable);
}
