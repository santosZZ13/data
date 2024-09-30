package org.data.sofa.repository.impl;

import org.data.dto.sf.GetHistoryFetchEventDto;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryFetchEventRepository {

	GetHistoryFetchEventDto.GetHistoryFetchEventData findAllByStatusAndStartTimestampBetween(String status, LocalDateTime fromDate, LocalDateTime toDate,
																							   PageRequest pageRequest);

	List<Integer> getAllIds();
	boolean isExistByTeamId(Integer eventId);
	void saveHistoryEventWithIds(List<Integer> ids);
}
