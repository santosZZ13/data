package org.data.eightBet.controller;

import lombok.AllArgsConstructor;
import org.data.eightBet.service.EightXBetService;
import org.data.common.model.GenericResponseWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EightXBetController {

	private final EightXBetService  eightXBetService;


	@GetMapping("/api/eightXBet/inplay")
	public GenericResponseWrapper getScheduledEventInPlay() {
		return eightXBetService.getScheduledEventInPlay();
	}
}
