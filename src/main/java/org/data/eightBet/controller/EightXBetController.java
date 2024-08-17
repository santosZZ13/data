package org.data.eightBet.controller;

import lombok.AllArgsConstructor;
import org.data.eightBet.service.EightXBetService;
import org.data.common.model.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class EightXBetController {

	private final EightXBetService eightXBetService;

	@GetMapping("/api/eightXBet/inplay")
	public BaseResponse getScheduledEventInPlay() {
		return eightXBetService.getScheduledEventInPlay();
	}

	@GetMapping("/api/data-service/ex/events")
	public BaseResponse getEventsByDate(@RequestParam String date) {
		return eightXBetService.getEventsByDate(date);
	}
}
