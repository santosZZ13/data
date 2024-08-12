package org.data.test;

import lombok.AllArgsConstructor;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.persistent.repository.HistoryFetchEventRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistoryService {
	private final HistoryFetchEventRepository historyFetchEventRepository;

	public HistoryFetchEventEntity save(HistoryFetchEventEntity historyFetchEventEntity) {
		return historyFetchEventRepository.save(historyFetchEventEntity);
	}
}
