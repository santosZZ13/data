package org.data.sofa.repository.impl;

import org.data.conts.EventStatus;
import org.data.sofa.dto.GetSofaEventHistoryDto;
import org.data.sofa.dto.SfEventsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SofaEventsTemplateRepository {
	List<GetSofaEventHistoryDto.HistoryScore> getHistoryScore(Integer teamId, EventStatus status, LocalDateTime from, LocalDateTime to);
	SfEventsDto.TeamDetails getTeamDetailsById(Integer id);
}
