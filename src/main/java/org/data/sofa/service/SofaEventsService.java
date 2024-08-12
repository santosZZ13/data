package org.data.sofa.service;

import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.GetStatisticsEventByIdDto;
import org.data.sofa.dto.SofaEventsByDateDTO;

public interface SofaEventsService {
	GenericResponseWrapper getAllScheduleEventsByDate(SofaEventsByDateDTO.Request request);
	GenericResponseWrapper fetchDataForTeamWithId(Integer id);
	GenericResponseWrapper getHistoryFromTeamId(Integer teamId);
	GenericResponseWrapper getStatisticsTeamFromTeamId(GetStatisticsEventByIdDto.Request request);
}
