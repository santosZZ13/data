package org.data.sofa.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.common.model.SortDto;
import org.data.config.FetchEventScheduler;
import org.data.dto.sf.*;
import org.data.service.fetch.FetchSofaEvent;
import org.data.service.fetch.FetchSofaEventImpl;
import org.data.service.sap.SapService;
import org.data.common.model.BaseResponse;
import org.data.sofa.exception.NotFoundEventException;
import org.data.sofa.mapper.SofaEventMapper;
import org.data.sofa.repository.impl.HistoryFetchEventRepository;
import org.data.sofa.repository.impl.SofaEventsTemplateRepository;
import org.data.sofa.response.EventChildResponse;
import org.data.sofa.response.EventsResponse;
import org.data.sofa.service.SofaEventsService;
import org.data.util.RestConnector;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;


import static org.data.dto.sf.GetSofaEventsByDateDto.*;

@Service
@AllArgsConstructor
@Log4j2
public class SofaEventsServiceImpl implements SofaEventsService {
	private final SapService sapService;
	private final RestConnector restConnector;
	private final FetchEventScheduler fetchEventScheduler;
	private final FetchSofaEventImpl fetchSofaEventImpl;
	private final SofaEventsTemplateRepository sofaEventsTemplateRepository;
	public final FetchSofaEvent fetchSofaEvent;
	private final HistoryFetchEventRepository historyFetchEventRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final SofaEventMapper sofaEventMapper;

	@Override
	public GetEventScheduledDto.Response getAllScheduleEventsByDate(GetEventScheduledDto.Request request) {

		CompletableFuture<EventsResponse> scheduledEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					final String key = "sofa" + request.getDate();
					if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
						log.info("#getAllScheduleEventsByDate - [Found] scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
						return (EventsResponse) redisTemplate.opsForValue().get(key);
					} else {
						log.info("#getAllScheduleEventsByDate - [Fetching] scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
						EventsResponse sofaEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate(), EventsResponse.class);
						redisTemplate.opsForValue().set(key, sofaEventsResponse);
						return sofaEventsResponse;
					}
				});


		CompletableFuture<EventsResponse> inverseScheduledEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					final String key = "sofa" + request.getDate() + "inverse";
					if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
						log.info("#getAllScheduleEventsByDate - [Found] inverse scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
						return (EventsResponse) redisTemplate.opsForValue().get(key);
					} else {
						log.info("#getAllScheduleEventsByDate - [Fetching] inverse scheduled events for date: [{}] in [Thread: {}]", request.getDate(), Thread.currentThread().getName());
						EventsResponse sofaEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + request.getDate() + SCHEDULED_EVENTS_INVERSE, EventsResponse.class);
						redisTemplate.opsForValue().set(key, sofaEventsResponse);
						return sofaEventsResponse;
					}
				});


		return scheduledEventsResponseFuture
				.thenCombine(inverseScheduledEventsResponseFuture, (scheduledEventsResponse, inverseScheduledEventsResponse) -> {
					String date = request.getDate();
					LocalDateTime requestDate = TimeUtil.convertStringToLocalDateTime(date);
					List<GetEventScheduledDto.ScheduledEventDto> eventDetails = getEventDetails(scheduledEventsResponse, inverseScheduledEventsResponse, requestDate);
					eventDetails.sort(Comparator.comparing(GetEventScheduledDto.ScheduledEventDto::getKickOffMatch));
					log.info("#getAllScheduleEventsByDate - eventDetails size: [{}]", eventDetails.size());
					return eventDetails;
				})
				.thenApplyAsync(eventDetails -> {
					CompletableFuture.runAsync(() -> {
						List<Integer> ids = getIdForFetchEventHistory(eventDetails);
						if (!ids.isEmpty()) {
							CompletableFuture.runAsync(() -> historyFetchEventRepository.saveHistoryEventWithIds(ids));
						} else {
							log.info("#getAllScheduleEventsByDate - No ids to fetch historical events");
						}
					});
					return eventDetails;
				})
				.thenApply(eventDto -> GetEventScheduledDto.Response
						.builder()
						.data(GetEventScheduledDto.ScheduledEventByDate.builder()
								.events(eventDto)
								.build())
						.build())
				.join();
	}

	@Override
	public BaseResponse fetchDataForTeamWithId(Integer id) {
		if (!historyFetchEventRepository.isExistByTeamId(id)) {
			fetchSofaEvent.fetchHistoricalMatchesForId(id);
			return BaseResponse.builder()
					.code("")
					.msg("Fetch event history for id: " + id)
//					.data("Success")
					.build();
		}
		return BaseResponse.builder()
				.code("")
				.msg("Fetch event history for id: " + id)
//				.data("Already fetched")
				.build();
	}

	@Override
	public GetSofaEventHistoryDto.Response getHistoryEventsFromTeamId(GetSofaEventHistoryDto.Request request) {

		int teamId = request.getTeamId();


		if (!historyFetchEventRepository.isExistByTeamId(teamId)) {
			throw new NotFoundEventException("", "History fetch event not found", "");
		}

		List<GetSofaEventHistoryDto.HistoryScore> historyScore = sofaEventsTemplateRepository
				.getHistoryScore(teamId, request.getStatus(), request.getFrom(), request.getTo());

		SfEventsDto.TeamDetails teamDetailsById = sofaEventsTemplateRepository.getTeamDetailsById(teamId);

		historyScore.forEach(hs -> {
			if (hs.getHomeScore().isScoreEmpty()) {
				hs.setHomeScore(null);
			}
			if (hs.getAgainstScore().isScoreEmpty()) {
				hs.setAgainstScore(null);
			}
		});

		return GetSofaEventHistoryDto.Response.builder()
				.data(GetSofaEventHistoryDto.GetSofaEventHistoryData.builder()
						.teamDetails(teamDetailsById)
						.historyScores(historyScore)
						.totalMatches(historyScore.size())
						.build())
				.build();
	}

	@Override
	public GetStatisticsEventByIdDto.Response getStatisticsTeamFromTeamId(GetStatisticsEventByIdDto.Request request) {
		LocalDateTime fromDateRequest = TimeUtil.convertStringToLocalDateTime(request.getFrom());
		LocalDateTime toRequestRequest = TimeUtil.convertStringToLocalDateTime(request.getTo());

		if (!historyFetchEventRepository.isExistByTeamId(request.getId())) {
			throw new NotFoundEventException("", "The team with id: " + request.getId() + " does not have historical events", "");
		}

		List<GetSofaEventHistoryDto.HistoryScore> historyScores = sofaEventsTemplateRepository.getHistoryScore(request.getId(), null, fromDateRequest, toRequestRequest);

		int totalMatches = 0;
		int totalScoreFullTime = 0;
		int totalScoreFirstHalf = 0;
		int totalScoreSecondHalf = 0;
		int hasScoreInFirstHalf = 0;
		int hasScoreInSecondHalf = 0;

		for (GetSofaEventHistoryDto.HistoryScore historyScore : historyScores) {
			if (!historyScore.getStatus().equals("notstarted")) {
				GetSofaEventHistoryDto.Score homeScore = historyScore.getHomeScore();

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
		String ratioScoreInSecond = (float) hasScoreInSecondHalf / (float) totalMatches * 100 + "%";


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


		return GetStatisticsEventByIdDto.Response.builder()
				.data(GetStatisticsEventByIdDto.GetStatisticsEventData.builder()
						.statistics(statistics)
						.build())
				.build();
	}

	@Override
	public GetHistoryFetchEventDto.Response getHistoryFetchEvent(GetHistoryFetchEventDto.Request request) {

		Integer pageNumber = request.getPageNumber();
		Integer pageSize = request.getPageSize();
		String sortField = request.getSortField();
		SortDto.Direction sortDirection = request.getSortDirection();

		String status = request.getStatus();
		LocalDateTime fromDate = TimeUtil.convertStringToLocalDateTime(request.getFromDate());
		LocalDateTime toDate = TimeUtil.convertStringToLocalDateTime(request.getToDate());
		PageRequest pageRequest = PageRequest
				.of(pageNumber, pageSize);

		if (!Objects.isNull(sortField)) {
			pageRequest.withSort(sortDirection.equals(SortDto.Direction.ASC) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
		}

		GetHistoryFetchEventDto.GetHistoryFetchEventData allByStatusAndStartTimestampBetween = historyFetchEventRepository.findAllByStatusAndStartTimestampBetween(status, fromDate, toDate, pageRequest);

		return GetHistoryFetchEventDto.Response
				.builder()
				.data(allByStatusAndStartTimestampBetween)
				.build();
	}


	private @NotNull List<GetEventScheduledDto.ScheduledEventDto> getEventDetails(EventsResponse scheduledEventsResponse,
																  EventsResponse inverseScheduledEventsResponse,
																  LocalDateTime requestDate) {
		List<EventChildResponse> eventResponses = scheduledEventsResponse.getEvents();
		List<EventChildResponse> inverseEventResponses = inverseScheduledEventsResponse.getEvents();
		eventResponses.addAll(inverseEventResponses);

		List<GetEventScheduledDto.ScheduledEventDto> scheduledEventsDto = new ArrayList<>();

		for (EventChildResponse responseEventResponse : eventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(responseEventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				GetEventScheduledDto.ScheduledEventDto scheduledEventDto = sofaEventMapper.scheduledEventDto(responseEventResponse);
				scheduledEventsDto.add(scheduledEventDto);
			}
		}
		return scheduledEventsDto;
	}


	private List<Integer> getIdForFetchEventHistory(List<GetEventScheduledDto.ScheduledEventDto> eventDetails) {
		return eventDetails.stream()
				.flatMap(eventDetail -> {
					Integer idTeamHome = eventDetail.getHomeDetails().getIdTeam();
					Integer idTeamAway = eventDetail.getAwayDetails().getIdTeam();
					return Stream.of(idTeamHome, idTeamAway);
				})
				.distinct()
				.toList();
	}
}
