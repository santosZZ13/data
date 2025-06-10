package org.data.repository.sofa;

import org.data.dto.common.SofaMatchDto;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;

import java.util.List;

public interface SofaRepository {
	void saveSofaScheduledMatches(List<SofaMatchResponseDetailDto> matchesDto);

	List<SofaMatchDto> findSofaScheduledMatchesByName(String name);

	List<SofaMatchDto> findSofaMatchByName(String name);

	List<SofaMatchResponseDetailDto> getMatchesByDate(String date);
}
