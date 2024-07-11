package org.data.tournament.service;

import org.data.tournament.dto.GenericResponseWrapper;
import org.data.tournament.dto.ScheduledEventDTO;
import org.data.tournament.dto.ScheduledEventsResponse;

import java.util.List;

public interface ScheduledEventsService {
	GenericResponseWrapper getAllScheduleEventsByDate(ScheduledEventDTO.Request request);
}
