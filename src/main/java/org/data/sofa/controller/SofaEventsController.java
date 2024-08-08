package org.data.sofa.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaEventsByDateDTO;
import org.data.sofa.service.SofaEventsService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class SofaEventsController {

	private final SofaEventsService sofaEventsService;


	@PostMapping("/api/data-service/sofa/getScheduledEventByDate")
	public GenericResponseWrapper getScheduleEventsByDate(@RequestBody SofaEventsByDateDTO.Request request) {
		return sofaEventsService.getAllScheduleEventsByDate(request);
	}

	@GetMapping("/api/data-service/sofa/fetch/id")
	public GenericResponseWrapper fetchId(@PathVariable Integer id) {
		return sofaEventsService.fetchId(id);
	}


	@GetMapping("/api/data-service/sofa/team/{id}")
	public GenericResponseWrapper getHistory(@PathVariable Integer id) {
		return sofaEventsService.getHistoryFromTeamId(id);
	}
}
