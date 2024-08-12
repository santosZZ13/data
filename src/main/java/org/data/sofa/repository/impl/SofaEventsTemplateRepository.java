package org.data.sofa.repository.impl;

import org.data.sofa.dto.GetSofaEventHistoryDTO;
import org.data.sofa.dto.SofaEventsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface SofaEventsTemplateRepository {
	List<GetSofaEventHistoryDTO.HistoryScore> getHistoryScore(Integer teamId, LocalDateTime from, LocalDateTime to);
	SofaEventsDTO.TeamDetails getTeamDetailsById(Integer id);
}
