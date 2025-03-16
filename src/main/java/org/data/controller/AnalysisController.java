package org.data.controller;


import lombok.AllArgsConstructor;
import org.data.dto.analysis.GetAnalysisDto;
import org.data.service.AnalysisService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@AllArgsConstructor
@RestController
@RequestMapping("/api/matches")
public class AnalysisController {

	private final AnalysisService analysisService;


	@GetMapping("/analyze")
	GetAnalysisDto.Response getAnalysis(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		return analysisService.getAnalysisByDate(date);
	}
}
