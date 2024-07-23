package org.data.sofa.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.SofaScheduledEventByDateDTO;
import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.sofa.repository.impl.ScheduledEventsRepository;
import org.data.sofa.service.ScheduledEventsService;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.data.sofa.dto.SofaScheduledEventByDateDTO.*;
import static org.data.sofa.dto.SofaScheduledEventsResponse.*;

@Service
@AllArgsConstructor
@Log4j2
public class ScheduledEventsServiceImpl implements ScheduledEventsService {

	private final ScheduledEventsRepository scheduledEventsRepository;
	private final SapService sapService;

	@Override
	public GenericResponseWrapper getAllScheduleEventsByDate(Request request) {

		SofaScheduledEventsResponse sofaScheduledEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate(), SofaScheduledEventsResponse.class);
		SofaScheduledEventsResponse sofaInverseScheduledEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS
				+ request.getDate() + SCHEDULED_EVENTS_INVERSE, SofaScheduledEventsResponse.class);

		String date = request.getDate();
		LocalDateTime requestDate = TimeUtil.convertStringToLocalDateTime(date);

		List<EventDetail> eventDetails = getEventDetails(sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse, requestDate);


		Response response = Response
				.builder()
				.eventDetails(eventDetails)
				.build();


		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(response)
				.build();
	}

	private @NotNull List<EventDetail> getEventDetails(SofaScheduledEventsResponse sofaScheduledEventsResponse,
													   SofaScheduledEventsResponse sofaInverseScheduledEventsResponse,
													   LocalDateTime requestDate) {

		List<EventResponse> sofaScheduledEventsResponseEventResponses = sofaScheduledEventsResponse.getEvents();
		List<EventResponse> sofaInverseScheduledEventsResponseEventResponses = sofaInverseScheduledEventsResponse.getEvents();
		List<EventDetail> eventDetails = new ArrayList<>();

		getEventDetailsByDate(requestDate, sofaScheduledEventsResponseEventResponses, eventDetails);
		getEventDetailsByDate(requestDate, sofaInverseScheduledEventsResponseEventResponses, eventDetails);
		return eventDetails;
	}

	private void getEventDetailsByDate(LocalDateTime requestDate,
									   List<EventResponse> sofaScheduledEventsResponseEventResponses,
									   List<EventDetail> eventDetails) {
		for (EventResponse responseEventResponse : sofaScheduledEventsResponseEventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(responseEventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				EventDetail eventDetail = populatedToEventDetail(responseEventResponse);
				eventDetails.add(eventDetail);
			}
		}
	}


	private EventDetail populatedToEventDetail(EventResponse eventResponse) {

		ScheduledEventsCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
		ScheduledEventsCommonResponse.Score eventResponseAwayScore = eventResponse.getAwayScore();

		return EventDetail.builder()
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeName(eventResponse.getHomeTeam().getName())
				.awayName(eventResponse.getAwayTeam().getName())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}


}
