package org.data.tournament.service;

import org.data.tournament.dto.ScheduledEventsResponse;

import java.util.List;

public interface ScheduledEventsService {
	List<ScheduledEventsResponse> getAllScheduleEventsByDate();
}
