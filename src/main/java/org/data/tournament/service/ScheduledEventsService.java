package org.data.tournament.service;

import org.data.common.model.GenericResponseWrapper;
import org.data.tournament.dto.ScheduledEventDTO;

public interface ScheduledEventsService {
	GenericResponseWrapper getAllScheduleEventsByDate(ScheduledEventDTO.Request request);
}
