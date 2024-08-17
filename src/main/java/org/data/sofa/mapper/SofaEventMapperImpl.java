package org.data.sofa.mapper;

import org.data.sofa.dto.SofaCommonResponse;
import org.data.sofa.dto.SofaEventsDto;
import org.data.sofa.dto.SofaEventsResponse;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SofaEventMapperImpl implements SofaEventMapper {
	@Override
	public SofaEventsDto.EventDto toEventDto(SofaEventsResponse.EventResponse eventResponse) {
		SofaCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
		SofaCommonResponse.Score eventResponseAwayScore = eventResponse.getAwayScore();

		return SofaEventsDto.EventDto.builder()
				.id(eventResponse.getId())
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeDetails(SofaEventsDto.TeamDetails.builder()
						.idTeam(eventResponse.getHomeTeam().getId())
						.name(eventResponse.getHomeTeam().getName())
						.build())
				.awayDetails(SofaEventsDto.TeamDetails.builder()
						.idTeam(eventResponse.getAwayTeam().getId())
						.name(eventResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}
}
