package org.data.mapper;

import org.data.dto.history.HistoryFetchCommonDto;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.dto.sf.GetHistoryFetchEventDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HistoryFetchEventMapper {
	HistoryFetchCommonDto.HistoryFetchEventDto toHistoryFetchEventDto(HistoryFetchEventEntity historyFetchEventEntity);
}
