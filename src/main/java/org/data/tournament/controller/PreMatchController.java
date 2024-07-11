package org.data.tournament.controller;

import lombok.AllArgsConstructor;
import org.data.tournament.service.PreMatchService;
import org.data.tournament.service.ScheduledEventsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PreMatchController {
	private final PreMatchService preMatchService;
	private final ScheduledEventsService scheduledEventsService;

	@GetMapping("/api/football")
	public void getPreMatches() {
		preMatchService.getAllTour();
	}


}
