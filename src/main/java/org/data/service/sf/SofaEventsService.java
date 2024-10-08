package org.data.service.sf;

import org.data.common.model.BaseResponse;
import org.data.dto.sf.GetEventScheduledDto;
import org.data.dto.sf.GetHistoryFetchEventDto;
import org.data.dto.sf.GetSofaEventHistoryDto;
import org.data.dto.sf.GetStatisticsEventByIdDto;

public interface SofaEventsService {
	GetEventScheduledDto.Response getAllScheduleEventsByDate(GetEventScheduledDto.Request request);









	BaseResponse fetchDataForTeamWithId(Integer id);
	GetSofaEventHistoryDto.Response getHistoryEventsFromTeamId(GetSofaEventHistoryDto.Request request);
	GetStatisticsEventByIdDto.Response getStatisticsTeamFromTeamId(GetStatisticsEventByIdDto.Request request);

	GetHistoryFetchEventDto.Response getHistoryFetchEvent(GetHistoryFetchEventDto.Request request);
}
