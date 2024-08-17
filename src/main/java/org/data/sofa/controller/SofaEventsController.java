package org.data.sofa.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.BaseResponse;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.data.sofa.dto.GetSofaEventHistoryDto;
import org.data.sofa.dto.GetStatisticsEventByIdDto;
import org.data.sofa.dto.GetSofaEventsByDateDto;
import org.data.sofa.service.SofaEventsService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SofaEventsController {

	private final SofaEventsService sofaEventsService;

	private static final String PRE_FIX_API = "/api/v1/data-service/sport/football";

	@PostMapping(PRE_FIX_API + "/scheduled-events")
	public BaseResponse getScheduleEventsByDate(@RequestBody GetSofaEventsByDateDto.Request request) {
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
