package org.data.tournament.service;

import org.data.tournament.dto.GenericResponseWrapper;
import org.data.tournament.dto.ScheduledEventDTO;

public interface ScheduledEventsService {
	GenericResponseWrapper getAllScheduleEventsByDate(ScheduledEventDTO.Request request);
}
