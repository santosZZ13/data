package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.GetMatchesExByDate;
import org.data.dto.ImportMatchesJsonFile;
import org.data.service.ex.ExService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/exBet")
public class EightXBetController {

	private final ExService exService;

	@PostMapping("/importMatchesJsonFile")
	public ImportMatchesJsonFile.Response importMatchesJsonFile(@RequestPart("file") MultipartFile request) {
		return exService.getDataFile(request);
	}

	@GetMapping("/matches")
	public GetMatchesExByDate.Response getMachesByDate(@Param("date") String date) {
		return exService.getMatchesByDate(date);
	}
}
