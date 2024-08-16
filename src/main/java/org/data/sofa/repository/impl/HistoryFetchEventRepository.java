package org.data.sofa.repository.impl;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryFetchEventRepository {
	List<GetHistoryFetchEventDto.Response> getHistoryFetchEventByStatus(GetHistoryFetchEventDto.Request request);

	List<GetHistoryFetchEventDto.HistoryFetchEventDto> findAllByStatusAndStartTimestampBetween(String status, LocalDateTime fromDate, LocalDateTime toDate,
																				   PageRequest pageRequest);

	Optional<HistoryFetchEventEntity> findByTeamId(Integer id);
}
