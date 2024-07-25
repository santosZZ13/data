package org.data.sofa.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.properties.ConnectionProperties;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.sofa.repository.impl.ScheduledEventsRepository;
import org.data.sofa.service.ScheduledEventsService;
import org.data.util.RestConnector;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import static org.data.sofa.dto.SofaScheduledEventByDateDTO.*;
import static org.data.sofa.dto.SofaScheduledEventsResponse.*;

@Service
@AllArgsConstructor
@Log4j2
public class ScheduledEventsServiceImpl implements ScheduledEventsService {

	private final ScheduledEventsRepository scheduledEventsRepository;
	private final SapService sapService;
	private final RestConnector restConnector;
	private static final int BATCH_SIZE = 100; // Adjust batch size as needed


	@Override
	public GenericResponseWrapper getAllScheduleEventsByDate(Request request) {

		SofaScheduledEventsResponse sofaScheduledEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate(), SofaScheduledEventsResponse.class);
		SofaScheduledEventsResponse sofaInverseScheduledEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS
				+ request.getDate() + SCHEDULED_EVENTS_INVERSE, SofaScheduledEventsResponse.class);

		String date = request.getDate();
		LocalDateTime requestDate = TimeUtil.convertStringToLocalDateTime(date);

		List<EventDetail> eventDetails = getEventDetails(sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse, requestDate);

		// sort eventDetails by kickOffMatch DESC
		eventDetails.sort(Comparator.comparing(EventDetail::getKickOffMatch));

		List<Integer> ids = eventDetails.stream().map(EventDetail::getId).toList();
		fetchHistoricalMatches(ids);

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


	private void fetchHistoricalMatches(List<Integer> ids) {
		List<CompletableFuture<Void>> futures = ids.stream()
				.map(id -> CompletableFuture.runAsync(() -> fetchHistoricalMatchesForId(id)))
				.toList();

		// Wait for all futures to complete
		CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
		try {
			allOf.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error fetching historical matches", e);
			Thread.currentThread().interrupt();
		}
	}

	private void fetchHistoricalMatchesForId(Integer id) {
		int initLast = 0;
		int intNext = 0;
		boolean isLast = true;
		boolean isNext = true;

		List<SofaScheduledEventsResponse> sofaScheduledEventsResponses = new ArrayList<>();
		List<EventResponse> eventResponses = new ArrayList<>();


		while (true) {
			if (isLast) {
				SofaScheduledEventsResponse sofaScheduledEventsResponseLast = restConnector.restGet(ConnectionProperties.Host.SOFASCORE,
						SCHEDULED_EVENT_TEAM_LAST, SofaScheduledEventsResponse.class, Arrays.asList(id, initLast));
				if (sofaScheduledEventsResponseLast.getHasNextPage()) {
					initLast++;
					sofaScheduledEventsResponses.add(sofaScheduledEventsResponseLast);
				} else {
					isLast = false;
				}
			}

			if (isNext) {
				SofaScheduledEventsResponse sofaScheduledEventsResponseNext = restConnector.restGet(ConnectionProperties.Host.SOFASCORE,
						SCHEDULED_EVENT_TEAM_NEXT, SofaScheduledEventsResponse.class, Arrays.asList(id, intNext));

				if (sofaScheduledEventsResponseNext.getHasNextPage()) {
					sofaScheduledEventsResponses.add(sofaScheduledEventsResponseNext);
					intNext++;
				} else {
					isNext = false;
				}
			}

			if (!isLast && !isNext) {
				break;
			}
		}


		sofaScheduledEventsResponses.forEach(sofaScheduledEventsResponse -> {
			List<EventResponse> events = sofaScheduledEventsResponse.getEvents();
			eventResponses.addAll(events);

			if (eventResponses.size() >= BATCH_SIZE) {
				scheduledEventsRepository.saveEvents(new ArrayList<>(eventResponses));
				eventResponses.clear();
			}
		});

		scheduledEventsRepository.saveEvents(eventResponses);
	}

}
