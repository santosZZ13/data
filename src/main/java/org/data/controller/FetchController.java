package org.data.controller;

import lombok.AllArgsConstructor;
import org.data.dto.FetchSfEventDto;
import org.data.service.fetch.FetchSfEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FetchController {

	private final FetchSfEventService fetchSfEventService;


	@GetMapping(FetchSfEventDto.FETCH_SF_EVENT)
	public FetchSfEventDto.Response fetchSfEventByTeamId(@PathVariable Integer teamId) {
		return fetchSfEventService.fetchSfEventByTeamId(teamId);
	}

}
