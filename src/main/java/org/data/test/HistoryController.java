package org.data.test;

import lombok.AllArgsConstructor;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HistoryController {

	private final HistoryService historyService;

	@PostMapping("/api/test/history")
	public HistoryFetchEventEntity save(@RequestBody HistoryFetchEventEntity historyFetchEventEntity) {
		return historyService.save(historyFetchEventEntity);
	}
}
