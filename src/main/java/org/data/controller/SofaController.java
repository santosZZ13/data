package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.sf.SaveScheduledMatchDto;
import org.data.service.sf.SofaScheduledMatchService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
