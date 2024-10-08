package org.data.repository.sf.impl;

import org.data.persistent.entity.ScheduledEventsSofaScoreEntity;
import org.data.dto.sf.SfEventsResponse.EventResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SfEventRepository {
	ScheduledEventsSofaScoreEntity saveSfEvent(List<EventResponse> eventResponses, int teamId);

	List<EventResponse> getAllEvents();

	EventResponse saveEvent(EventResponse eventResponse);

	EventResponse getAllEventByDate(LocalDateTime date, Pageable pageable);

	List<EventResponse> getEventsById(Integer id);
}
