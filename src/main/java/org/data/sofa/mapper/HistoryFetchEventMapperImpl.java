package org.data.sofa.mapper;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.springframework.stereotype.Component;

@Component
public class HistoryFetchEventMapperImpl implements HistoryFetchEventMapper {
	@Override
	public GetHistoryFetchEventDto.HistoryFetchEventDto toHistoryFetchEventDto(HistoryFetchEventEntity historyFetchEventEntity) {
		return null;
	}
}
