package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.BaseResponse;
import org.data.dto.sf.GetEventScheduledDto;
import org.data.dto.sf.GetHistoryFetchEventDto;
import org.data.dto.sf.GetSofaEventHistoryDto;
import org.data.dto.sf.GetStatisticsEventByIdDto;
import org.data.service.sf.SofaEventsService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SfController {

	private final SofaEventsService sofaEventsService;

	private static final String PRE_FIX_API = "/api/v1/sport/football";

	@PostMapping(PRE_FIX_API + GetEventScheduledDto.GET_SCHEDULED_EVENT_BY_DATE_API)
	public GetEventScheduledDto.Response getScheduleEventsByDate(@RequestBody GetEventScheduledDto.Request request) {
		return sofaEventsService.getAllScheduleEventsByDate(request);
	}
















	@PostMapping( PRE_FIX_API + "/events")
	public GetSofaEventHistoryDto.Response getHistoryEvents(@RequestBody GetSofaEventHistoryDto.Request request) {
		return sofaEventsService.getHistoryEventsFromTeamId(request);
	}

	@PostMapping(PRE_FIX_API + "/history")
	public GetHistoryFetchEventDto.Response getHistoryFetchEvent(@RequestBody GetHistoryFetchEventDto.Request request) {
		return sofaEventsService.getHistoryFetchEvent(request);
	}

	@GetMapping(PRE_FIX_API + "/fetch/{id}")
	public BaseResponse fetchId(@PathVariable Integer id) {
		return sofaEventsService.fetchDataForTeamWithId(id);
	}

	@PostMapping(PRE_FIX_API + "/statistics")
	public GetStatisticsEventByIdDto.Response getSofaStatistics(@RequestBody GetStatisticsEventByIdDto.Request request) {
		return sofaEventsService.getStatisticsTeamFromTeamId(request);
	}

}
