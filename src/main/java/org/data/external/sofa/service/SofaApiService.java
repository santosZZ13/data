package org.data.external.sofa.service;

import org.data.external.sofa.model.SofaMatchResponseDetail;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SofaApiService {
	List<SofaMatchResponseDetail> getMatchesByIdAndLimit(Integer teamId, Integer limit);

	List<SofaMatchResponseDetail> getMatchesByDate(String date);

	Map<Integer, List<SofaMatchResponseDetail>> getHistoriesByTeamIds(Set<Integer> teamIds);
}
