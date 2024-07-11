package org.data.tournament.repository.impl;

import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.tournament.persistent.entity.ScheduledEventsEntity;

import java.util.List;

public interface ScheduledEventsRepository {
	List<ScheduledEventsEntity> saveEvents(List<ScheduledEventsResponse.Event> events);

	List<ScheduledEventsResponse.Event> getAllEvents();

	ScheduledEventsResponse.Event saveEvent(ScheduledEventsResponse.Event event);

}
