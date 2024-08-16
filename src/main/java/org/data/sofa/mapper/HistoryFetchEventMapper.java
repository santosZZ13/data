package org.data.sofa.mapper;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoryFetchEventMapper {
	GetHistoryFetchEventDto.HistoryFetchEventDto toHistoryFetchEventDto(HistoryFetchEventEntity historyFetchEventEntity);
}
