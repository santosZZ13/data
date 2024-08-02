package org.data.common.converter;

import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.SofaScheduledEventByDateDTO;
import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.util.TimeUtil;

import java.util.Objects;

public class Converter {
	public static SofaScheduledEventByDateDTO.EventDetail populatedToEventDetail(SofaScheduledEventsResponse.EventResponse eventResponse) {

		ScheduledEventsCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
		ScheduledEventsCommonResponse.Score eventResponseAwayScore = eventResponse.getAwayScore();

		return SofaScheduledEventByDateDTO.EventDetail.builder()
				.id(eventResponse.getId())
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeDetails(SofaScheduledEventByDateDTO.TeamDetails.builder()
						.idTeam(eventResponse.getHomeTeam().getId())
						.name(eventResponse.getHomeTeam().getName())
						.build())
				.awayDetails(SofaScheduledEventByDateDTO.TeamDetails.builder()
						.idTeam(eventResponse.getAwayTeam().getId())
						.name(eventResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}
}
