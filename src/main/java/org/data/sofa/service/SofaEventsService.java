package org.data.sofa.service;

import org.data.common.model.BaseResponse;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.data.sofa.dto.GetSofaEventHistoryDto;
import org.data.sofa.dto.GetStatisticsEventByIdDto;
import org.data.sofa.dto.GetSofaEventsByDateDto;

public interface SofaEventsService {
	BaseResponse getAllScheduleEventsByDate(GetSofaEventsByDateDto.Request request);
	BaseResponse fetchDataForTeamWithId(Integer id);
	GetSofaEventHistoryDto.Response getHistoryEventsFromTeamId(GetSofaEventHistoryDto.Request request);
	GetStatisticsEventByIdDto.Response getStatisticsTeamFromTeamId(GetStatisticsEventByIdDto.Request request);

	GetHistoryFetchEventDto.Response getHistoryFetchEvent(GetHistoryFetchEventDto.Request request);
}
