package org.data.service.history;


import org.data.conts.FetchStatus;
import org.data.dto.history.HistoryFetchEventDto;

import java.util.List;

public interface HistoryFetchEventService {
	HistoryFetchEventDto getHistoryFetchEventByTeamId(Integer idTeam);
	List<HistoryFetchEventDto> getAllHistoryFetchEvents();
	void saveHistoryFetchEvent(HistoryFetchEventDto historyFetchEventDto);
	void saveHistoryFetchEvents(List<HistoryFetchEventDto> historyFetchEventDto);
	void saveHistoryEventWithIds(List<Integer> ids);
	List<Integer> getHistoryFetchEventWithStatus(FetchStatus status);
}
