package org.data.test;

import lombok.AllArgsConstructor;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.persistent.repository.HistoryFetchEventMongoRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HistoryService {
	private final HistoryFetchEventMongoRepository historyFetchEventMongoRepository;

	public HistoryFetchEventEntity save(HistoryFetchEventEntity historyFetchEventEntity) {
		return historyFetchEventMongoRepository.save(historyFetchEventEntity);
	}
}
