package org.data.common.converter;

import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.SofaScheduledEventByDateDTO;
import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

	public static void getEventDetailsByDate(LocalDateTime requestDate,
									   List<SofaScheduledEventsResponse.EventResponse> sofaScheduledEventsResponseEventResponses,
									   List<SofaScheduledEventByDateDTO.EventDetail> eventDetails) {
		for (SofaScheduledEventsResponse.EventResponse responseEventResponse : sofaScheduledEventsResponseEventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(responseEventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				SofaScheduledEventByDateDTO.EventDetail eventDetail = populatedToEventDetail(responseEventResponse);
				eventDetails.add(eventDetail);
			}
		}
	}

	public static @NotNull List<SofaScheduledEventByDateDTO.EventDetail> getEventDetails(SofaScheduledEventsResponse sofaScheduledEventsResponse,
																				   SofaScheduledEventsResponse sofaInverseScheduledEventsResponse,
																				   LocalDateTime requestDate) {

		List<SofaScheduledEventsResponse.EventResponse> sofaScheduledEventsResponseEventResponses = sofaScheduledEventsResponse.getEvents();
		List<SofaScheduledEventsResponse.EventResponse> sofaInverseScheduledEventsResponseEventResponses = sofaInverseScheduledEventsResponse.getEvents();
		List<SofaScheduledEventByDateDTO.EventDetail> eventDetails = new ArrayList<>();

		getEventDetailsByDate(requestDate, sofaScheduledEventsResponseEventResponses, eventDetails);
		getEventDetailsByDate(requestDate, sofaInverseScheduledEventsResponseEventResponses, eventDetails);
		return eventDetails;
	}
}
