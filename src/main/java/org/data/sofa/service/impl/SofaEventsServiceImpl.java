package org.data.sofa.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.common.exception.ApiException;
import org.data.config.FetchEventScheduler;
import org.data.persistent.entity.HistoryFetchEventEntity;
import org.data.persistent.repository.HistoryFetchEventRepository;
import org.data.service.fetch.FetchSofaEvent;
import org.data.service.fetch.FetchSofaEventImpl;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.*;
import org.data.sofa.repository.impl.SofaEventsTemplateRepository;
import org.data.sofa.service.SofaEventsService;
import org.data.util.RestConnector;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;


import static org.data.sofa.dto.SofaEventsByDateDTO.*;
import static org.data.sofa.dto.SofaEventsResponse.*;

@Service
@AllArgsConstructor
@Log4j2
public class SofaEventsServiceImpl implements SofaEventsService {
	private final SapService sapService;
	private final RestConnector restConnector;
	private final FetchEventScheduler fetchEventScheduler;
	private final FetchSofaEventImpl fetchSofaEventImpl;
	private final HistoryFetchEventRepository historyFetchEventRepository;
	private final SofaEventsTemplateRepository sofaEventsTemplateRepository;
	public final FetchSofaEvent fetchSofaEvent;


	@Override
	public GenericResponseWrapper getAllScheduleEventsByDate(Request request) {
		//TODO:
		// check data if exist in redis.
		// if exist return data from redis.
		// if not exist, fetch data from sap and save to redis.

		CompletableFuture<SofaEventsResponse> sofaScheduledEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getAllScheduleEventsByDate - [Fetching] scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate(), SofaEventsResponse.class);
				});

		CompletableFuture<SofaEventsResponse> sofaInverseScheduledEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getAllScheduleEventsByDate - [Fetching] inverse scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate() + SCHEDULED_EVENTS_INVERSE, SofaEventsResponse.class);
				});

		return sofaScheduledEventsResponseFuture
				.thenCombine(sofaInverseScheduledEventsResponseFuture, (sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse) -> {
					//TODO:
					// cache sofaScheduledEventsResponseFuture and sofaInverseScheduledEventsResponseFuture to redis

					String date = request.getDate();
					LocalDateTime requestDate = TimeUtil.convertStringToLocalDateTime(date);
					List<SofaEventsDTO.EventDTO> eventDetails = getEventDetails(sofaScheduledEventsResponse, sofaInverseScheduledEventsResponse, requestDate);
					eventDetails.sort(Comparator.comparing(SofaEventsDTO.EventDTO::getKickOffMatch));
					log.info("#getAllScheduleEventsByDate - eventDetails size: [{}]", eventDetails.size());
					return eventDetails;
				})
				.thenApply(eventDetails -> {

					List<Integer> ids = getIdForFetchEventHistory(eventDetails);

					if (ids.isEmpty()) {
						log.info("#getAllScheduleEventsByDate - No ids to fetch historical events");
					} else {
						CompletableFuture.runAsync(() -> fetchSofaEventImpl.fetchHistoricalMatches(ids));
					}

					return Response.builder()
							.eventDTOS(eventDetails)
							.build();
				})
				.thenApply(response -> GenericResponseWrapper
						.builder()
						.code("")
						.msg("")
						.data(response)
						.build())
				.join();
	}

	private List<Integer> getIdForFetchEventHistory(List<SofaEventsDTO.EventDTO> eventDetails) {
		List<Integer> ids = eventDetails.stream()
				.flatMap(eventDetail -> {
					Integer idTeamHome = eventDetail.getHomeDetails().getIdTeam();
					Integer idTeamAway = eventDetail.getAwayDetails().getIdTeam();
					return Stream.of(idTeamHome, idTeamAway);
				})
//							.distinct()
				.toList();
		ids.subList(0, 10);


		List<Integer> historyFetchEvents = historyFetchEventRepository.findAll().stream()
				.map(HistoryFetchEventEntity::getTeamId).toList();

		List<Integer> idsToFetch = new ArrayList<>();
		for (Integer id : ids) {
			if (!historyFetchEvents.contains(id)) {
				idsToFetch.add(id);
			}
		}

		return idsToFetch;
	}


	@Override
	public GenericResponseWrapper fetchDataForTeamWithId(Integer id) {
		if (!alreadyFetchDataForTeamID(id)) {
			fetchSofaEvent.fetchHistoricalMatchesForId(id);
			return GenericResponseWrapper.builder()
					.code("")
					.msg("Fetch event history for id: " + id)
					.data("Success")
					.build();
		}
		return GenericResponseWrapper.builder()
				.code("")
				.msg("Fetch event history for id: " + id)
				.data("Already fetched")
				.build();
	}

	@Override
	public GenericResponseWrapper getHistoryFromTeamId(Integer teamId) {

		if (!alreadyFetchDataForTeamID(teamId)) {
			throw new ApiException("", "History fetch event not found", "");
		}

		List<GetSofaEventHistoryDTO.HistoryScore> historyScore = sofaEventsTemplateRepository.getHistoryScore(teamId, null, null);
		SofaEventsDTO.TeamDetails teamDetailsById = sofaEventsTemplateRepository.getTeamDetailsById(teamId);
		historyScore.forEach(hs -> {
			if (hs.getHomeScore().isScoreEmpty()) {
				hs.setHomeScore(null);
			}
			if (hs.getAgainstScore().isScoreEmpty()) {
				hs.setAgainstScore(null);
			}
		});
		return GenericResponseWrapper.builder()
				.code("")
				.msg("")
				.data(GetSofaEventHistoryDTO.Response.builder()
						.teamDetails(teamDetailsById)
						.historyScores(historyScore)
						.totalMatches(historyScore.size())
						.build())
				.build();

	}

	@Override
	public GenericResponseWrapper getStatisticsTeamFromTeamId(GetStatisticsEventByIdDto.Request request) {
		LocalDateTime fromDateRequest = TimeUtil.convertStringToLocalDateTime(request.getFrom());
		LocalDateTime toRequestRequest = TimeUtil.convertStringToLocalDateTime(request.getTo());

		if (!alreadyFetchDataForTeamID(request.getId())) {
			throw new ApiException("", "The team with id: " + request.getId() + " does not have historical events", "");
		}

		List<GetSofaEventHistoryDTO.HistoryScore> historyScores = sofaEventsTemplateRepository.getHistoryScore(request.getId(), fromDateRequest, toRequestRequest);

		int totalMatches = 0;
		int totalScoreFullTime = 0;
		int totalScoreFirstHalf = 0;
		int totalScoreSecondHalf = 0;
		int hasScoreInFirstHalf = 0;
		int hasScoreInSecondHalf = 0;

		for (GetSofaEventHistoryDTO.HistoryScore historyScore : historyScores) {
			if (!historyScore.getStatus().equals("notstarted")) {
				GetSofaEventHistoryDTO.Score homeScore = historyScore.getHomeScore();

				int normalTimeScore = Objects.isNull(homeScore) || homeScore.isScoreEmpty() || Objects.isNull(homeScore.getNormaltime())
						? 0 : homeScore.getNormaltime();
				totalScoreFullTime += normalTimeScore;

				int scoreInFirstHalf = Objects.isNull(homeScore) || homeScore.isScoreEmpty() || Objects.isNull(homeScore.getPeriod1())
						? 0 : homeScore.getPeriod1();
				totalScoreFirstHalf += scoreInFirstHalf;

				if (scoreInFirstHalf >= 1) {
					hasScoreInFirstHalf++;
				}

				int homeScoreInPeriod2 = Objects.isNull(homeScore) || homeScore.isScoreEmpty() || Objects.isNull(homeScore.getPeriod2())
						? 0 : homeScore.getPeriod2();
				if (homeScoreInPeriod2 >= 1) {
					hasScoreInSecondHalf++;
				}

				totalScoreSecondHalf += homeScoreInPeriod2;
				totalMatches++;
			}
		}

		float averageGoalPerMach = (float) totalScoreFullTime / (float) totalMatches;
		float averageGoalInFirstHalf = (float) totalScoreFirstHalf / (float) totalMatches;
		float averageGoalInSecondHalf = (float) totalScoreSecondHalf / (float) totalMatches;
		String ratioScoreInFirst = ((float) hasScoreInFirstHalf / (float) totalMatches) * 100 + "%";
		String ratioScoreInSecond = (float)  hasScoreInSecondHalf / (float) totalMatches * 100 + "%";


		GetStatisticsEventByIdDto.Statistics statistics = GetStatisticsEventByIdDto.Statistics.builder()
				.totalMatches(totalMatches)
				.averageGoalPerMach(averageGoalPerMach)
				.averageGoalInFirstHalf(averageGoalInFirstHalf)
				.averageGoalInSecondHalf(averageGoalInSecondHalf)
				.scoreInFirstHalf(GetStatisticsEventByIdDto.ScoreDetail.builder()
						.count(hasScoreInFirstHalf)
						.ratio(ratioScoreInFirst)
						.build())
				.scoreInSecondHalf(GetStatisticsEventByIdDto.ScoreDetail.builder()
						.count(hasScoreInSecondHalf)
						.ratio(ratioScoreInSecond)
						.build())
				.build();


		return GenericResponseWrapper.builder()
				.code("")
				.msg("")
				.data(statistics)
				.build();
	}


	private Boolean alreadyFetchDataForTeamID(Integer id) {
		Optional<HistoryFetchEventEntity> byIdTeam = historyFetchEventRepository.findByTeamId(id);
		if (byIdTeam.isPresent()) {
			return true;
		}
		return false;
	}


	private @NotNull List<SofaEventsDTO.EventDTO> getEventDetails(SofaEventsResponse sofaEventsResponse,
																  SofaEventsResponse sofaInverseScheduledEventsResponse,
																  LocalDateTime requestDate) {

		List<EventResponse> sofaScheduledEventsResponseEventResponses = sofaEventsResponse.getEvents();
		List<EventResponse> sofaInverseScheduledEventsResponseEventResponses = sofaInverseScheduledEventsResponse.getEvents();
		List<SofaEventsDTO.EventDTO> eventDetails = new ArrayList<>();

		getEventDetailsByDate(requestDate, sofaScheduledEventsResponseEventResponses, eventDetails);
		getEventDetailsByDate(requestDate, sofaInverseScheduledEventsResponseEventResponses, eventDetails);
		return eventDetails;
	}

	private void getEventDetailsByDate(LocalDateTime requestDate,
									   List<EventResponse> sofaScheduledEventsResponseEventResponses,
									   List<SofaEventsDTO.EventDTO> eventDetails) {
		for (EventResponse responseEventResponse : sofaScheduledEventsResponseEventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(responseEventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				SofaEventsDTO.EventDTO eventDetail = populatedToEventDetail(responseEventResponse);
				eventDetails.add(eventDetail);
			}
		}
	}


	private SofaEventsDTO.EventDTO populatedToEventDetail(EventResponse eventResponse) {

		SofaCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
		SofaCommonResponse.Score eventResponseAwayScore = eventResponse.getAwayScore();

		return SofaEventsDTO.EventDTO.builder()
				.id(eventResponse.getId())
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeDetails(SofaEventsDTO.TeamDetails.builder()
						.idTeam(eventResponse.getHomeTeam().getId())
						.name(eventResponse.getHomeTeam().getName())
						.build())
				.awayDetails(SofaEventsDTO.TeamDetails.builder()
						.idTeam(eventResponse.getAwayTeam().getId())
						.name(eventResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}
}
