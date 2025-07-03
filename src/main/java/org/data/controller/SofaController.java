package org.data.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.sf.GetScheduledMatchByName;
import org.data.dto.sf.GetSofaMatchesByDate;
import org.data.dto.sf.SaveScheduledMatchDto;
import org.data.service.SofaScheduledMatchService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sofa")
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
public class SofaController {

	private final SofaScheduledMatchService sofaScheduledMatchService;

	@PostMapping("/scheduled-matches")
	public SaveScheduledMatchDto.Response saveScheduledMatches(@RequestBody SaveScheduledMatchDto.Request request) {
		log.info("Received request to save scheduled matches with {} matches", request.getMatches().size());
		return sofaScheduledMatchService.saveScheduledMatches(request);
	}

	@GetMapping("/find-matches")
	public GetScheduledMatchByName.Response findMatchesByName(@RequestParam String name) {
		return sofaScheduledMatchService.findMatchByName(name);
	}

	@GetMapping("/matches")
	public GetSofaMatchesByDate.Response getMatchesByDate(@RequestParam("date") String date) {
		return sofaScheduledMatchService.getMatchesByDate(date);
	}


}
