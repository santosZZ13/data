package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.ImportMatchesJsonFile;
import org.data.service.ex.ExService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
}
