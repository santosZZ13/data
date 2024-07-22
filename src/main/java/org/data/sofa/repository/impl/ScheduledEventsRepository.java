package org.data.sofa.repository.impl;

import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledEventsRepository {
	List<ScheduledEventsSofaScoreEntity> saveEvents(List<SofaScheduledEventsResponse.EventResponse> eventResponses);

	List<SofaScheduledEventsResponse.EventResponse> getAllEvents();

	SofaScheduledEventsResponse.EventResponse saveEvent(SofaScheduledEventsResponse.EventResponse eventResponse);

	SofaScheduledEventsResponse.EventResponse getAllEventByDate(LocalDateTime date, Pageable pageable);
}
