package org.data.repository;

import org.data.dto.common.sofa.SofaMatchDto;
import org.data.external.sofa.model.SofaMatchResponseDetail;

import java.util.List;

public interface SofaRepository {
	void saveSofaScheduledMatches(List<SofaMatchResponseDetail> matchesDto);

	List<SofaMatchDto> findSofaScheduledMatchesByName(String name);

	List<SofaMatchDto> findSofaMatchByName(String name);

	List<SofaMatchResponseDetail> getMatchesByDate(String date);
}
