package org.data.service.fetch;

import org.data.dto.FetchSfEventDto;

import java.util.List;

public interface FetchSfEventService {
	void fetchHistoricalMatches(List<Integer> ids);
	void fetchHistoricalMatchesForId(Integer id);

	FetchSfEventDto.Response fetchSfEventByTeamId(Integer teamId);
}
