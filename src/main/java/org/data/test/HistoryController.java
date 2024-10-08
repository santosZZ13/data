package org.data.test;

import lombok.AllArgsConstructor;
import org.data.service.fetch.FetchSfEventService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@AllArgsConstructor
public class HistoryController {

	private final FetchSfEventService historyService;
	private final FetchSfEventService fetchSofaEvent;

//	@PostMapping("/api/test/history")
//	public HistoryFetchEventEntity save(@RequestBody HistoryFetchEventEntity historyFetchEventEntity) {
//		return historyService.save(historyFetchEventEntity);
//	}

	@PostMapping("/api/test/history/fetch")
	public void fetch(@RequestBody Integer[] ids) {
		fetchSofaEvent.fetchHistoricalMatches(Arrays.asList(ids));
	}
}
