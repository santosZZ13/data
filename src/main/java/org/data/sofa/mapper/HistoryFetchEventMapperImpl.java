package org.data.sofa.mapper;

import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.sofa.dto.GetHistoryFetchEventDto;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class HistoryFetchEventMapperImpl implements HistoryFetchEventMapper {
	@Override
	public GetHistoryFetchEventDto.HistoryFetchEventDto toHistoryFetchEventDto(HistoryFetchEventEntity historyFetchEventEntity) {

		if (Objects.isNull(historyFetchEventEntity)) {
			return null;
		}

		return GetHistoryFetchEventDto.HistoryFetchEventDto.builder()
				.teamId(historyFetchEventEntity.getTeamId())
				.timeElapsed(historyFetchEventEntity.getTimeElapsed())
				.total(historyFetchEventEntity.getTotal())
				.fetchStatus(historyFetchEventEntity.getFetchStatus())
				.createdDate(historyFetchEventEntity.getCreatedDate())
				.build();
	}
}
