package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.ex.GetMatchesExByDateDto;
import org.data.dto.ex.ImportMatchesJsonFile;
import org.data.dto.ex.SaveMatchesDto;
import org.data.service.ex.ExService;
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

	@GetMapping("/matches")
	public GetMatchesExByDateDto.Response getMachesByDate(@Param("date") String[] date) {
		return exService.getMatchesByDate(date, false);
	}

	@PostMapping("/matchesFavorite")
	public SaveMatchesDto.Response saveMatchesFavorite(@RequestBody SaveMatchesDto.Request request) {
		return exService.saveMatchesFavorite(request, true);
	}

	@GetMapping("/matchesFavorite")
	public GetMatchesExByDateDto.Response getFavoriteMatches(@Param("date") String[] date) {
		return exService.getMatchesByDate(date, true);
	}


//	@PostMapping("/matchesNotFavorite")
//	public SaveMatchesDto.Response saveMatchesNotFavorite(@RequestBody SaveMatchesDto.Request request) {
//		return exService.saveMatchesFavorite(request, false);
//	}

}
