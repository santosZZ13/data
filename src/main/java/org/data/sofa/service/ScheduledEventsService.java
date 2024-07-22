package org.data.sofa.service;

import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaScheduledEventByDateDTO;

public interface ScheduledEventsService {
	GenericResponseWrapper getAllScheduleEventsByDate(SofaScheduledEventByDateDTO.Request request);
}
