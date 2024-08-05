package org.data.sofa.service;

import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaEventsByDateDTO;

public interface ScheduledEventsService {
	GenericResponseWrapper getAllScheduleEventsByDate(SofaEventsByDateDTO.Request request);
	GenericResponseWrapper fetchId(Integer id);
}
