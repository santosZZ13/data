package org.data.eightBet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.common.exception.ApiException;
import org.data.eightBet.dto.EightXBetCommonResponse;
import org.data.eightBet.dto.EventsByDateDTO;
import org.data.eightBet.dto.EightXBetEventsResponse;
import org.data.eightBet.dto.EightXBetEventsResponse.EightXBetTournamentResponse;
import org.data.eightBet.repository.EightXBetRepository;
import org.data.persistent.entity.EventsEightXBetEntity;
import org.data.service.fetch.FetchSofaEvent;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaCommonResponse;
import org.data.sofa.dto.SofaEventsDTO;
import org.data.sofa.dto.SofaEventsResponse;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.data.eightBet.dto.EightXBetEventDTO.*;
import static org.data.eightBet.dto.EightXBetEventsResponse.*;
import static org.data.sofa.dto.SofaEventsByDateDTO.SCHEDULED_EVENTS;
import static org.data.sofa.dto.SofaEventsByDateDTO.SCHEDULED_EVENTS_INVERSE;
import static org.data.util.TeamUtils.areTeamNamesEqual;


@Service
@AllArgsConstructor
@Log4j2
public class EightXBetServiceImpl implements EightXBetService {

	private final SapService sapService;
	private final EightXBetRepository eightXBetRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final FetchSofaEvent fetchSofaEvent;

	@Override
	public GenericResponseWrapper getScheduledEventInPlay() {

//		EightXBetEventsResponse eightXBetEventsResponse = sapService.restEightXBetGet(EventInPlayDTO.EIGHT_X_BET,
//				EventInPlayDTO.queryParams(),
//				EightXBetEventsResponse.class);
//
//		Data data = eightXBetEventsResponse.getData();
//
//		if (Objects.isNull(data.getTournaments())) {
//			throw new ApiException("Errors", "", "No data found for scheduled events in play");
//		}
//
//		EventInPlayDTO.Response response = populateScheduledEventInPlayResponseToDTO(data.getTournaments());
//
//		if (!Objects.equals(response.getTntSize(), 0)) {
//			List<EventsEightXBetEntity> scheduledEventsEightXBetEntities = eightXBetRepository.saveTournamentResponse(data.getTournaments());
//		}
//
//		return GenericResponseWrapper
//				.builder()
//				.code("")
//				.msg("")
//				.data(response)
//				.build();
		return null;

	}

//	@Cacheable(value = "sofaEvents", key = "#date")
//	public SofaEventsResponse getSofaEventsResponse(String date) {
//		log.info("#getEventsByDate - fetching [Sofa events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
//		return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SofaEventsResponse.class);
//	}
//
//	@Cacheable(value = "sofaInverseEvents", key = "#date")
//	public SofaEventsResponse getSofaInverseEventsResponse(String date) {
//		log.info("#getEventsByDate - fetching [Inverse events events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
//		return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SofaEventsResponse.class);
//	}

	@Override
	public GenericResponseWrapper getEventsByDate(String date) {

		//TODO: Need to call API
		CompletableFuture<EightXBetEventsResponse> eightXBetEventResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					ObjectMapper objectMapper = new ObjectMapper();
					EightXBetEventsResponse eightXBetEventsResponse = null;
					try {
						eightXBetEventsResponse = objectMapper.readValue(new File("src/main/resources/response.json"),
								EightXBetEventsResponse.class);
					} catch (Exception e) {
						throw new ApiException("Errors", "", "Error occurred while reading json file");
					}
					return eightXBetEventsResponse;
				});


		CompletableFuture<SofaEventsResponse> sofaEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					String cacheKey = "sofaEvents::" + date;
					SofaEventsResponse cachedResponse = (SofaEventsResponse) redisTemplate.opsForValue().get(cacheKey);
					if (cachedResponse != null) {
						log.info("#getEventsByDate - [Found] cached sofa events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						return cachedResponse;
					} else {
						log.info("#getEventsByDate - [Fetching] sofa events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						SofaEventsResponse sofaEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SofaEventsResponse.class);
						redisTemplate.opsForValue().set(cacheKey, sofaEventsResponse);
						return sofaEventsResponse;
					}
				});

		CompletableFuture<SofaEventsResponse> sofaInverseEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {

					String cacheKey = "sofaInverseEvents::" + date;
					SofaEventsResponse cachedResponse = (SofaEventsResponse) redisTemplate.opsForValue().get(cacheKey);
					if (cachedResponse != null) {
						log.info("#getEventsByDate - [Found] cached sofa inverse events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						return cachedResponse;
					} else {
						log.info("#getEventsByDate - [Fetching] sofa inverse events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						SofaEventsResponse sofaEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SofaEventsResponse.class);
						redisTemplate.opsForValue().set(cacheKey, sofaEventsResponse);
						return sofaEventsResponse;
					}
				});

		return sofaEventsResponseFuture.thenCombineAsync(sofaInverseEventsResponseFuture, (sofaEventsResponse, sofaInverseEventsResponse) -> {
					log.info("#getEventsByDate - [Merging] {{sofa events}} with {{inverse sofa events}} for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					List<SofaEventsDTO.EventDTO> eventDTOS = new ArrayList<>();
					List<SofaEventsResponse.EventResponse> eventsResponse = sofaEventsResponse.getEvents();
					List<SofaEventsResponse.EventResponse> eventsInverseResponse = sofaInverseEventsResponse.getEvents();
					eventsResponse.addAll(eventsInverseResponse);
					getEventDTOByDate(eventsResponse, eventDTOS, TimeUtil.convertStringToLocalDateTime(date));
					return eventDTOS;
				})
				.thenCombine(eightXBetEventResponseFuture, (eventDTOS, eightXBetEventResponse) -> {
					log.info("#getEventsByDate - [Merging]  {{eventDTS}} with {{eightXBetEventResponse}} for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					Data data = eightXBetEventResponse.getData();
					if (!data.getTournaments().isEmpty()) {

						Map<Integer, List<EightXBetTournamentDTO>> eightXBetTournamentDTOMap = convertToEightXBetTournamentDTO(data.getTournaments(),
								TimeUtil.convertStringToLocalDateTime(date));

						Map.Entry<Integer, List<EightXBetTournamentDTO>> eightXBetTournamentDTOEntry = eightXBetTournamentDTOMap
								.entrySet()
								.iterator()
								.next();

						List<Integer> idsForSofaToFetch = new ArrayList<>();
						List<EightXBetTournamentDTO> tournamentDtoWithId = new ArrayList<>();
						List<EightXBetTournamentDTO> tournamentDTOWithNoId = new ArrayList<>();
						int totalMatchesFirst = 0;
						int totalMatchesSecond = 0;

						for (EightXBetTournamentDTO eightXBetTournamentDTO : eightXBetTournamentDTOEntry.getValue()) {

							List<EightXBetMatchDTO> eightXBetMatchDTOSFirst = new ArrayList<>();
							List<EightXBetMatchDTO> eightXBetMatchDTOSSecond = new ArrayList<>();

							for (EightXBetMatchDTO eightXBetMatchDTO : eightXBetTournamentDTO.getMatches()) {
								SofaEventsDTO.EventDTO eventDTO = CheckEightXBetMatcInEventDTO(eightXBetMatchDTO, eventDTOS);
								if (!Objects.isNull(eventDTO)) {
									idsForSofaToFetch.add(eventDTO.getHomeDetails().getIdTeam());
									idsForSofaToFetch.add(eventDTO.getAwayDetails().getIdTeam());
									SofaEvent sofaEvent = buildSofaEvent(eventDTO);
									eightXBetMatchDTO.setSofaDetail(sofaEvent);
									eightXBetMatchDTOSFirst.add(eightXBetMatchDTO);
									totalMatchesFirst++;
								} else {
									eightXBetMatchDTOSSecond.add(eightXBetMatchDTO);
									totalMatchesSecond++;
								}
							}



							if (!eightXBetMatchDTOSFirst.isEmpty()) {
								EightXBetTournamentDTO eightXBetTournamentDTOFirst = EightXBetTournamentDTO
										.builder()
										.tntName(eightXBetTournamentDTO.getTntName())
										.count(eightXBetMatchDTOSFirst.size())
										.matches(eightXBetMatchDTOSFirst)
										.build();
								tournamentDtoWithId.add(eightXBetTournamentDTOFirst);
							}

							if (!eightXBetMatchDTOSSecond.isEmpty()) {
								EightXBetTournamentDTO eightXBetTournamentDTOSecond = EightXBetTournamentDTO
										.builder()
										.tntName(eightXBetTournamentDTO.getTntName())
										.count(eightXBetMatchDTOSSecond.size())
										.matches(eightXBetMatchDTOSSecond)
										.build();
								tournamentDTOWithNoId.add(eightXBetTournamentDTOSecond);
							}
						}

						CompletableFuture.runAsync(() -> fetchSofaEvent.fetchHistoricalMatches(idsForSofaToFetch));

						EventsByDateDTO.TournamentDTO tournamentFirst = EventsByDateDTO.TournamentDTO.builder()
								.eightXBetTournamentDto(tournamentDtoWithId)
								.matchSize(totalMatchesFirst)
								.tntSize(tournamentDtoWithId.size())
								.build();

						EventsByDateDTO.TournamentDTO tournamentSecond = EventsByDateDTO.TournamentDTO.builder()
								.eightXBetTournamentDto(tournamentDTOWithNoId)
								.matchSize(totalMatchesSecond)
								.tntSize(tournamentDTOWithNoId.size())
								.build();

						return EventsByDateDTO
								.Response
								.builder()
								.totalMatches(eightXBetTournamentDTOEntry.getKey())
								.totalTournaments(eightXBetTournamentDTOEntry.getValue().size())
								.tournamentsFirst(tournamentFirst)
								.tournamentsSecond(tournamentSecond)
								.build();
					}
					return null;
				})
				.thenApply(response -> GenericResponseWrapper
						.builder()
						.code("")
						.msg("")
						.data(response)
						.build())
				.whenComplete((result, throwable) -> {
					if (Objects.nonNull(throwable)) {
						log.error("#getEventsByDate - Error occurred: {}", throwable.getMessage());
					}
				})
				.join();
	}


	private SofaEvent buildSofaEvent(SofaEventsDTO.@NotNull EventDTO eventDTO) {
		Team firstTeam = Team.builder()
				.id(eventDTO.getHomeDetails().getIdTeam())
				.name(eventDTO.getHomeDetails().getName())
				.build();
		Team secondTeam = Team.builder()
				.id(eventDTO.getAwayDetails().getIdTeam())
				.name(eventDTO.getAwayDetails().getName())
				.build();
		return SofaEvent.builder()
				.firstTeam(firstTeam)
				.secondTeam(secondTeam)
				.build();
	}

	private void getEventDTOByDate(List<SofaEventsResponse.EventResponse> eventResponses,
								   List<SofaEventsDTO.EventDTO> eventDTOS,
								   LocalDateTime requestDate) {
		for (SofaEventsResponse.EventResponse responseEventResponse : eventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(responseEventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				SofaEventsDTO.EventDTO eventDTO = populatedToEventDTO(responseEventResponse);
				eventDTOS.add(eventDTO);
			}
		}
	}

	public SofaEventsDTO.EventDTO populatedToEventDTO(SofaEventsResponse.EventResponse eventResponse) {

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


	private SofaEventsDTO.EventDTO CheckEightXBetMatcInEventDTO(EightXBetMatchDTO eightXBetMatchDTO, List<SofaEventsDTO.EventDTO> eventDTOS) {

		String eightXBetNameHome = eightXBetMatchDTO.getHomeName();
		String eightXBetAwayName = eightXBetMatchDTO.getAwayName();
		LocalDateTime kickoffTime = eightXBetMatchDTO.getKickoffTime();

		boolean isEqualNameFirst;
		boolean isEqualNameSecond;

		for (SofaEventsDTO.EventDTO eventDTO : eventDTOS) {
			isEqualNameFirst = false;
			isEqualNameSecond = false;

			String sofaNameHome = eventDTO.getHomeDetails().getName();
			String sofaAwayName = eventDTO.getAwayDetails().getName();
			LocalDateTime kickOffMatch = eventDTO.getKickOffMatch();

			if (areTeamNamesEqual(eightXBetNameHome, sofaNameHome) || areTeamNamesEqual(eightXBetAwayName, sofaNameHome)) {
				if (TimeUtil.isEqual(kickoffTime, kickOffMatch)) {
					isEqualNameFirst = true;
				}
			}

			if (areTeamNamesEqual(eightXBetNameHome, sofaAwayName) || areTeamNamesEqual(eightXBetAwayName, sofaAwayName)) {
				if (TimeUtil.isEqual(kickoffTime, kickOffMatch)) {
					isEqualNameSecond = true;
				}
			}

			if (isEqualNameFirst && isEqualNameSecond) {
				return eventDTO;
			}
		}
		return null;
	}


	@Override
	@Transactional
	public GenericResponseWrapper fetchEvents() {

//		eightXBetRepository.updateInplayEvent();

		EightXBetEventsResponse eightXBetEventsResponse = sapService.restEightXBetGet(EventsByDateDTO.GET_EVENTS_BY_DATE,
				EventsByDateDTO.queryParams(),
				EightXBetEventsResponse.class);

		Data data = eightXBetEventsResponse.getData();
		List<EightXBetTournamentResponse> tournaments = data.getTournaments();

		List<Integer> iidEventEntities = new ArrayList<>();
		Map<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> tournamentMatchResponseMap = new LinkedHashMap<>();

		if (!tournaments.isEmpty()) {
			List<EventsEightXBetEntity> eventsEntities = eightXBetRepository.getAllEventsEntity();
			eventsEntities.forEach(eventEntity -> iidEventEntities.add(eventEntity.getIId()));


			tournaments.forEach(tournament -> {
				List<EightXBetCommonResponse.EightXBetMatchResponse> matches = tournament.getMatches();
				matches.forEach(match -> {
					if (!iidEventEntities.contains(match.getIid())) {
						tournamentMatchResponseMap.put(tournament, match);
					}
				});
			});

			eightXBetRepository.saveMatchesMap(tournamentMatchResponseMap);
		}

		Collection<EightXBetCommonResponse.EightXBetMatchResponse> matches = tournamentMatchResponseMap.values();
		List<EightXBetMatchDTO> eightXBetMatchDTOS = new ArrayList<>();
		for (EightXBetCommonResponse.EightXBetMatchResponse match : matches) {
			eightXBetMatchDTOS.add(buildEightXBetMatchDTO(match));
		}


		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(eightXBetMatchDTOS)
				.build();
	}


	/**
	 * Convert EightXBetTournamentResponse to EightXBetTournamentDTO
	 *
	 * @param eightXBetTournamentResponses List of EightXBetTournamentResponse
	 * @return Map<Integer, List < EightXBetTournamentDTO>>
	 */
	private @NotNull @Unmodifiable Map<Integer, List<EightXBetTournamentDTO>> convertToEightXBetTournamentDTO(@NotNull List<EightXBetTournamentResponse> eightXBetTournamentResponses,
																											  LocalDateTime requestDate) {
		List<EightXBetTournamentDTO> eightXBetTournamentDTOS = new ArrayList<>();
		int totalMatches = 0;

		for (EightXBetTournamentResponse eightXBetTournamentResponse : eightXBetTournamentResponses) {

			String name = eightXBetTournamentResponse.getName();
			List<EightXBetMatchDTO> eightXBetMatchDTOS = new ArrayList<>();

			for (EightXBetCommonResponse.EightXBetMatchResponse eightXBetMatchRsp : eightXBetTournamentResponse.getMatches()) {
				if (!Objects.isNull(requestDate)) {
					LocalDateTime localDateTimeResponse = TimeUtil.convertUnixTimestampToLocalDateTime(eightXBetMatchRsp.getKickoffTime());
					if (requestDate.getDayOfMonth() == localDateTimeResponse.getDayOfMonth()) {
						EightXBetMatchDTO eightXBetMatchDTO = buildEightXBetMatchDTO(eightXBetMatchRsp);
						totalMatches++;
						eightXBetMatchDTOS.add(eightXBetMatchDTO);
					}
				} else {
					EightXBetMatchDTO eightXBetMatchDTO = buildEightXBetMatchDTO(eightXBetMatchRsp);
					totalMatches++;
					eightXBetMatchDTOS.add(eightXBetMatchDTO);
				}
			}

			if (!eightXBetMatchDTOS.isEmpty()) {
				EightXBetTournamentDTO eightXBetTournamentDTO = EightXBetTournamentDTO
						.builder()
						.tntName(name)
						.count(eightXBetMatchDTOS.size())
						.matches(eightXBetMatchDTOS)
						.build();

				eightXBetTournamentDTOS.add(eightXBetTournamentDTO);
			}
		}
		return Map.of(totalMatches, eightXBetTournamentDTOS);
	}


	private EightXBetMatchDTO buildEightXBetMatchDTO(EightXBetCommonResponse.EightXBetMatchResponse eightXBetMatchResponse) {
		EightXBetCommonResponse.EightXBetTeamResponse eightXBetMatchResponseHome = eightXBetMatchResponse.getHome();
		EightXBetCommonResponse.EightXBetTeamResponse eightXBetMatchResponseAway = eightXBetMatchResponse.getAway();
		long kickoffTime = eightXBetMatchResponse.getKickoffTime();
		return EightXBetMatchDTO.builder()
				.iid(eightXBetMatchResponse.getIid())
				.homeName(eightXBetMatchResponseHome.getName())
				.awayName(eightXBetMatchResponseAway.getName())
				.slug(eightXBetMatchResponse.getName())
				.inPlay(eightXBetMatchResponse.getInplay())
				.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTime))
				.build();
	}
}
