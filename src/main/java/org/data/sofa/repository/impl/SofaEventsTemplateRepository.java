package org.data.sofa.repository.impl;

import org.data.sofa.dto.GetSofaEventHistoryDTO;
import org.data.sofa.dto.SofaEventsDTO;

import java.util.List;

public interface SofaEventsTemplateRepository {
	List<GetSofaEventHistoryDTO.HistoryScore> getHistoryScore(Integer id);
	SofaEventsDTO.TeamDetails getTeamDetailsById(Integer id);
}
