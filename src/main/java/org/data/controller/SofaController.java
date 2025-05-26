package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.sf.GetScheduledMatchByName;
import org.data.dto.sf.SaveScheduledMatchDto;
import org.data.service.sf.SofaScheduledMatchService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sofa")
@CrossOrigin(origins = "http://localhost:3000")
public class SofaController {

	private final SofaScheduledMatchService sofaScheduledMatchService;

	@GetMapping("/scheduled-matches")
	public SaveScheduledMatchDto.Response saveScheduledMatches(SaveScheduledMatchDto.Request request) {
		return sofaScheduledMatchService.saveScheduledMatches(request);
	}

	@GetMapping("/find-matches")
	public GetScheduledMatchByName.Response findMatchesByName(@RequestParam String name) {
		return sofaScheduledMatchService.findMatchesByName(name);
	}
}
