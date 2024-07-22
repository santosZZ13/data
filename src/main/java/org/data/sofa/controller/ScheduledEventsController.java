package org.data.sofa.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaScheduledEventByDateDTO;
import org.data.sofa.service.ScheduledEventsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ScheduledEventsController {

	private final ScheduledEventsService scheduledEventsService;


	@PostMapping("/api/data-service/sofa/getScheduledEventByDate")
	public GenericResponseWrapper getScheduleEventsByDate(@RequestBody SofaScheduledEventByDateDTO.Request request) {
		return scheduledEventsService.getAllScheduleEventsByDate(request);
	}
}
