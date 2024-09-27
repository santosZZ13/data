package org.data.eightBet.controller;

import lombok.AllArgsConstructor;
import org.data.eightBet.dto.GetExBetEventByDate;
import org.data.eightBet.dto.GetExBetEventByDateWithDetails;
import org.data.eightBet.dto.ImportExBetFromFile;
import org.data.eightBet.service.ExBetService;
import org.data.common.model.BaseResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class EightXBetController {

	private final ExBetService exBetService;



	@PostMapping("/api/exBet/import")
	public ImportExBetFromFile.Response importScheduledEventFromFile(@RequestPart("file") MultipartFile file) {
		return exBetService.getDataFile(file);
	}

	@PostMapping("/api/exBet")
	public GetExBetEventByDate.Response getExBetEventByDate(@RequestBody GetExBetEventByDate.Request request) {
		return exBetService.getExBetEventByDate(request);
	}

	@PostMapping("/api/exBet/details")
	public GetExBetEventByDateWithDetails.Response getExBetEventByDateWithDetails(@RequestBody GetExBetEventByDateWithDetails.Request request) {
		return exBetService.getExBetEventByDateWithDetails(request);
	}



	@GetMapping("/api/eightXBet/inplay")
	public BaseResponse getScheduledEventInPlay() {
		return exBetService.getScheduledEventInPlay();
	}

	@GetMapping("/api/data-service/ex/events")
	public BaseResponse getEventsByDate(@RequestParam String date) {
		return exBetService.getEventsByDate(date);
	}
}
