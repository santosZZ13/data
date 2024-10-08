package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.ex.FetchExBetWithSfEventByDate;
import org.data.dto.ex.GetExBetEventByDate;
import org.data.dto.ex.GetExBetEventByDateWithDetails;
import org.data.dto.ex.ImportExBetFromFile;
import org.data.service.ex.ExService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ExController {

	private final ExService exService;


	@PostMapping("/api/exBet/import")
	public ImportExBetFromFile.Response importScheduledEventFromFile(@RequestPart("file") MultipartFile file) {
		return exService.getDataFile(file);
	}

	@PostMapping("/api/exBet")
	public GetExBetEventByDate.Response getExBetEventByDate(@RequestBody GetExBetEventByDate.Request request) {
		return exService.getExBetEventByDate(request);
	}

	@PostMapping("/api/exBet/details")
	public GetExBetEventByDateWithDetails.Response getExBetEventByDateWithDetails(@RequestBody GetExBetEventByDateWithDetails.Request request) {
		return exService.getExBetEventByDateWithDetails(request);
	}

	@PostMapping("/api/exBet/fetch")
	public FetchExBetWithSfEventByDate.Response fetchExBetWithSfEventByDate(@RequestBody FetchExBetWithSfEventByDate.Request request) {
		return exService.fetchExBetWithSfEventByDate(request);
	}


}
