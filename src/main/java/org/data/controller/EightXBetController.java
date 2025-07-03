package org.data.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.data.dto.ex.*;
import org.data.service.ExService;
import org.data.util.annotation.ValidDate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/exBet")
@CrossOrigin(origins = "http://localhost:3000")
public class EightXBetController {

	private final ExService exService;

	@PostMapping("/importMatchesJsonFile")
	public ImportMatchesJsonFile.Response importMatchesJsonFile(@RequestPart("file") MultipartFile request) {
		return exService.getDataFile(request);
	}

	@PostMapping("/matchesFavorite")
	public SaveMatchesDto.Response saveMatchesFavorite(@RequestBody SaveMatchesDto.Request request) {
		return exService.saveMatchesFavorite(request, true);
	}

	@GetMapping("/matchesFavorite")
	public GetMatchesExByDateDto.Response getFavoriteMatches(@Param("date") String[] date) {
		return exService.getMatchesByDate(date, true);
	}


//	@GetMapping("/matches")
//	public GetMatchesExByDateDto.Response getMachesByDate(@Param("date") String[] date) {
//		return exService.getMatchesByDate(date, false);
//	}


	@PostMapping("/matches")
	public SaveExBetMatchDto.Response saveMatches(@RequestParam("date") @ValidDate String date,
												  @RequestBody SaveExBetMatchDto.Request request) {
		return exService.saveMatches(request, date);
	}

	@PostMapping("/analyst")
	public GetAnalystDto.Response getAnalyst(@RequestBody @Valid GetAnalystDto.Request request) {
		return exService.getAnalyst(request);
	}
}
