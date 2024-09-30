package org.data.sofa.repository.impl;

import org.data.conts.EventStatus;
import org.data.dto.sf.GetSofaEventHistoryDto;
import org.data.dto.sf.SfEventsCommonDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SofaEventsTemplateRepository {
	List<GetSofaEventHistoryDto.HistoryScore> getHistoryScore(Integer teamId, EventStatus status, LocalDateTime from, LocalDateTime to);
	SfEventsCommonDto.TeamDetails getTeamDetailsById(Integer id);
}
