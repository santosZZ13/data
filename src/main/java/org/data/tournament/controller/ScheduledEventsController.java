package org.data.tournament.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.tournament.dto.ScheduledEventDTO;
import org.data.tournament.service.ScheduledEventsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ScheduledEventsController {

	private final ScheduledEventsService scheduledEventsService;


	@GetMapping("/data/api/v1/sport/football/scheduled-events/2024-07-10")
	public GenericResponseWrapper getScheduleEventsByDate(@RequestBody ScheduledEventDTO.Request request) {
		return scheduledEventsService.getAllScheduleEventsByDate(request);
	}
}
