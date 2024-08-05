package org.data.sofa.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaEventsByDateDTO;
import org.data.sofa.service.ScheduledEventsService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ScheduledEventsController {

	private final ScheduledEventsService scheduledEventsService;


	@PostMapping("/api/data-service/sofa/getScheduledEventByDate")
	public GenericResponseWrapper getScheduleEventsByDate(@RequestBody SofaEventsByDateDTO.Request request) {
		return scheduledEventsService.getAllScheduleEventsByDate(request);
	}

	@GetMapping("/api/data-service/sofa/fetch/id")
	public GenericResponseWrapper fetchId(@PathVariable Integer id) {
		return scheduledEventsService.fetchId(id);
	}
}
