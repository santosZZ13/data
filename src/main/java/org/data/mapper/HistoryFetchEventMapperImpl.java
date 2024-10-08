package org.data.mapper;

import org.data.dto.history.HistoryFetchCommonDto;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.dto.sf.GetHistoryFetchEventDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HistoryFetchEventMapperImpl implements HistoryFetchEventMapper {
	@Override
	public HistoryFetchCommonDto.HistoryFetchEventDto toHistoryFetchEventDto(HistoryFetchEventEntity historyFetchEventEntity) {

		if (Objects.isNull(historyFetchEventEntity)) {
			return null;
		}

		return HistoryFetchCommonDto.HistoryFetchEventDto.builder()
				.teamId(historyFetchEventEntity.getTeamId())
				.timeElapsed(historyFetchEventEntity.getTimeElapsed())
				.total(historyFetchEventEntity.getTotal())
				.fetchStatus(historyFetchEventEntity.getFetchStatus())
				.createdDate(historyFetchEventEntity.getCreatedDate())
				.build();
	}
}
