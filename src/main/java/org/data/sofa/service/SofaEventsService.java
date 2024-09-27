package org.data.sofa.service;

import org.data.common.model.BaseResponse;
import org.data.sofa.dto.*;

public interface SofaEventsService {
	GetEventScheduledDto.Response getAllScheduleEventsByDate(GetEventScheduledDto.Request request);









	BaseResponse fetchDataForTeamWithId(Integer id);
	GetSofaEventHistoryDto.Response getHistoryEventsFromTeamId(GetSofaEventHistoryDto.Request request);
	GetStatisticsEventByIdDto.Response getStatisticsTeamFromTeamId(GetStatisticsEventByIdDto.Request request);

	GetHistoryFetchEventDto.Response getHistoryFetchEvent(GetHistoryFetchEventDto.Request request);
}
