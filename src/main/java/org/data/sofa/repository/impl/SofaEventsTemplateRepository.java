package org.data.sofa.repository.impl;

import org.data.sofa.dto.GetSofaEventHistoryDTO;

import java.util.List;

public interface SofaEventsTemplateRepository {
	List<GetSofaEventHistoryDTO.HistoryScore> getHistoryScore(Integer id);
}
