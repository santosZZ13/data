package org.data.repository.sofa;

import org.data.dto.common.SofaMatchDto;
import org.data.dto.sf.GetScheduledMatchByName;

import java.util.List;

public interface SofaScheduledMatchRepository {
	void saveSofaScheduledMatches(List<SofaMatchDto> matchesDto);

	SofaMatchDto findSofaScheduledMatchByName(String name);
}
