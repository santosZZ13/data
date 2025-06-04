package org.data.repository.sofa;

import org.data.dto.common.SofaMatchDto;

import java.util.List;

public interface SofaScheduledMatchRepository {
	void saveSofaScheduledMatches(List<SofaMatchDto> matchesDto);

	List<SofaMatchDto> findSofaScheduledMatchesByName(String name);

	List<SofaMatchDto> findSofaScheduledMatchByName(String name);

	List<SofaMatchDto> getMatchesByDate(String date);
}
