package org.data.mapper;

import org.data.dto.sf.GetEventScheduledDto;
import org.data.response.sf.EventChildResponse;
import org.data.response.sf.ScoreResponse;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SofaEventMapperImpl implements SofaEventMapper {
	@Override
	public GetEventScheduledDto.ScheduledEventDto scheduledEventDto(EventChildResponse eventChildResponse) {
		ScoreResponse homeScoreResponse = eventChildResponse.getHomeScore();
		ScoreResponse awayScoreResponse = eventChildResponse.getAwayScore();

		return GetEventScheduledDto.ScheduledEventDto.builder()
				.id(eventChildResponse.getId())
				.tntName(eventChildResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventChildResponse.getSeason()) ? null : eventChildResponse.getSeason().getName())
				.round(Objects.isNull(eventChildResponse.getRoundInfo()) ? null : eventChildResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventChildResponse.getStatus()) ? null : eventChildResponse.getStatus().getDescription())
				.homeDetails(GetEventScheduledDto.TeamDetailsDto.builder()
						.idTeam(eventChildResponse.getHomeTeam().getId())
						.name(eventChildResponse.getHomeTeam().getName())
						.build())
				.awayDetails(GetEventScheduledDto.TeamDetailsDto.builder()
						.idTeam(eventChildResponse.getAwayTeam().getId())
						.name(eventChildResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventChildResponse.getStartTimestamp()))
				.build();
	}
}
