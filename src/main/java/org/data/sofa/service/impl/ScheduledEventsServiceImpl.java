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

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;


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

//		SofaScheduledEventsResponse sofaScheduledEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate(), SofaScheduledEventsResponse.class);
//		SofaScheduledEventsResponse sofaInverseScheduledEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS
//				+ request.getDate() + SCHEDULED_EVENTS_INVERSE, SofaScheduledEventsResponse.class);
//
//		String date = request.getDate();
//		LocalDateTime requestDate = TimeUtil.convertStringToLocalDateTime(date);
//
//		List<EventDetail> eventDetails = getEventDetails(sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse, requestDate);
//
//
//		List<EventResponse> eventResponses = new ArrayList<>();
//		eventResponses.addAll(sofaScheduledEventsResponse.getEvents());
//		eventResponses.addAll(sofaInverseScheduledEventsResponse.getEvents());
//		scheduledEventsRepository.saveEvents(eventResponses);
//
//		return GenericResponseWrapper
//				.builder()
//				.code("")
//				.msg("")
//				.data(Response.builder()
//						.eventDetails(eventDetails)
//						.build())
//				.build();

		//TODO:
		// check data if exist in redis.
		// if exist return data from redis.
		// if not exist, fetch data from sap and save to redis.

		CompletableFuture<SofaScheduledEventsResponse> sofaScheduledEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getAllScheduleEventsByDate - fetching scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate(), SofaScheduledEventsResponse.class);
				});


		CompletableFuture<SofaScheduledEventsResponse> sofaInverseScheduledEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getAllScheduleEventsByDate - fetching inverse scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate() + SCHEDULED_EVENTS_INVERSE, SofaScheduledEventsResponse.class);
				});

		List<EventDetail> eventDetailsList = sofaScheduledEventsResponseFuture
				.thenCombine(sofaInverseScheduledEventsResponseFuture, (sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse) -> {
					//TODO:
					// cache sofaScheduledEventsResponseFuture and sofaInverseScheduledEventsResponseFuture to redis

					String date = request.getDate();
					LocalDateTime requestDate = TimeUtil.convertStringToLocalDateTime(date);
					List<EventDetail> eventDetails = getEventDetails(sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse, requestDate);
					eventDetails.sort(Comparator.comparing(EventDetail::getKickOffMatch));
					log.info("#getAllScheduleEventsByDate - eventDetails size: [{}]", eventDetails.size());
					return eventDetails;
				})
				// get ids from eventDetails and return
				.thenApply(eventDetails -> {
					List<Integer> ids = eventDetails.stream()
							.flatMap(eventDetail -> {
								Integer idTeamHome = eventDetail.getHomeDetails().getIdTeam();
								Integer idTeamAway = eventDetail.getAwayDetails().getIdTeam();
								return Stream.of(idTeamHome, idTeamAway);
							})
//							.distinct()
							.toList();
//					List<Integer> ids = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
					CompletableFuture.runAsync(() -> fetchHistoricalMatches(ids));
					return eventDetails;
				})
				.join();


		// return
		Response response = Response.builder()
				.eventDetails(eventDetailsList)
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
				.id(eventResponse.getId())
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeDetails(TeamDetails.builder()
						.idTeam(eventResponse.getHomeTeam().getId())
						.name(eventResponse.getHomeTeam().getName())
						.build())
				.awayDetails(TeamDetails.builder()
						.idTeam(eventResponse.getAwayTeam().getId())
						.name(eventResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}


	private void fetchHistoricalMatches(List<Integer> ids) {
		List<Integer> idsTest = Arrays.asList(ids.get(0), ids.get(1));
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (Integer id : idsTest) {
			CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
				try {
					fetchHistoricalMatchesForId(id);
				} catch (Exception e) {
					log.error("Error fetching historical matches for id: {}", id, e);
				}
			});
			futures.add(voidCompletableFuture);
		}
		// Run asynchronously without waiting for completion
		CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[0]));
	}

	private void fetchHistoricalMatchesForId(Integer id) throws InterruptedException {
		// TODO:
		// Caching Strategies for APIs: https://medium.com/@satyendra.jaiswal/caching-strategies-for-apis-improving-performance-and-reducing-load-1d4bd2df2b44

		log.info("#fetchHistoricalMatchesForId - starting fetch for [id: {}] in [Thread : {}]", id, Thread.currentThread().getName());
		Instant start = Instant.now();
		int initLast = 0;
		int intNext = 0;
		boolean isLast = true;
		boolean isNext = true;

//		int timesTestForLast = 0;
//		int timesTestForNext = 0;

		List<SofaScheduledEventsResponse> sofaScheduledEventsResponses = new ArrayList<>();

		while (true) {
			if (isLast) {
//
//				Thread.sleep(500);
//				timesTestForLast++;

/*				if (timesTestForLast > 5) {
					isLast = false;
				}*/
				SofaScheduledEventsResponse sofaScheduledEventsResponseLast = restConnector.restGet(ConnectionProperties.Host.SOFASCORE,
						SCHEDULED_EVENT_TEAM_LAST, SofaScheduledEventsResponse.class, Arrays.asList(id, initLast));
				if (sofaScheduledEventsResponseLast.getHasNextPage()) {
					initLast++;
					sofaScheduledEventsResponses.add(sofaScheduledEventsResponseLast);
				} else {
					sofaScheduledEventsResponses.add(sofaScheduledEventsResponseLast);
					isLast = false;
				}
			}

			if (isNext) {

//				Thread.sleep(500);
//				timesTestForNext++;
//
//				if (timesTestForNext > 2) {
//					isNext = false;
//				}
				SofaScheduledEventsResponse sofaScheduledEventsResponseNext = restConnector.restGet(ConnectionProperties.Host.SOFASCORE,
						SCHEDULED_EVENT_TEAM_NEXT, SofaScheduledEventsResponse.class, Arrays.asList(id, intNext));

				if (sofaScheduledEventsResponseNext.getHasNextPage()) {
					sofaScheduledEventsResponses.add(sofaScheduledEventsResponseNext);
					intNext++;
				} else {
					sofaScheduledEventsResponses.add(sofaScheduledEventsResponseNext);
					isNext = false;
				}
			}

			if (!isLast && !isNext) {
				break;
			}
		}

		// sum of events
		sofaScheduledEventsResponses
				.stream()
				.map(sofaScheduledEventsResponse -> sofaScheduledEventsResponse.getEvents().size())
				.reduce(Integer::sum)
				.ifPresent(totalEvents -> log.info("Total events for id: {} is: {}", id, totalEvents));


		List<EventResponse> eventResponses = new ArrayList<>();
		sofaScheduledEventsResponses.forEach(sofaScheduledEventsResponse -> {
			List<EventResponse> events = sofaScheduledEventsResponse.getEvents();
			eventResponses.addAll(events);
		});


		saveEventBatch(eventResponses, id);
		Instant finish = Instant.now();
		log.info("#fetchHistoricalMatchesForId - time elapsed for [id: {}] in [{} ms]", id, TimeUtil.calculateTimeElapsed(start, finish));
		log.info("#fetchHistoricalMatchesForId - ended fetch for [id: {}]", id);

	}


	private void saveEventBatch(List<EventResponse> eventResponses, Integer idMatch) {
		log.info("#fetchHistoricalMatchesForId - saving events for [id: {}] with size: [{}]", idMatch, eventResponses.size());
		scheduledEventsRepository.saveEvents(eventResponses);
		//			if (eventResponses.size() >= BATCH_SIZE) {
//				log.info("#fetchHistoricalMatchesForId - saving events for [id: {}] with size: [{}]", id, eventResponses.size());
//				scheduledEventsRepository.saveEvents(eventResponses);
//				eventResponses.clear();
//			}
//		for (int i = 0; i < eventResponses.size(); i += BATCH_SIZE) {
//			int end = Math.min(eventResponses.size(), i + BATCH_SIZE);
//			List<EventResponse> batch = eventResponses.subList(i, end);
//			log.info("#fetchHistoricalMatchesForId - saving events for [id: {}] with size: [{}]", id, batch.size());
//			scheduledEventsRepository.saveEvents(batch);
//		}
	}
}
