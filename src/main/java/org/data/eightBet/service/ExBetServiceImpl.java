package org.data.eightBet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.common.exception.ApiException;
import org.data.eightBet.dto.*;
import org.data.eightBet.dto.EightXBetEventsResponse.EightXBetTournamentResponse;
import org.data.eightBet.repository.ExBetRepository;
import org.data.eightBet.response.ExBetResponse;
import org.data.eightBet.response.ExBetTournamentResponse;
import org.data.service.fetch.FetchSofaEvent;
import org.data.service.sap.SapService;
import org.data.common.model.BaseResponse;
import org.data.sofa.dto.SofaCommonResponse;
import org.data.sofa.dto.SfEventsDto;
import org.data.sofa.dto.SfEventsResponse;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.data.eightBet.dto.EightXBetEventDTO.*;
import static org.data.eightBet.dto.EightXBetEventsResponse.*;
import static org.data.sofa.dto.GetSofaEventsByDateDto.SCHEDULED_EVENTS;
import static org.data.sofa.dto.GetSofaEventsByDateDto.SCHEDULED_EVENTS_INVERSE;
import static org.data.util.TeamUtils.areTeamNamesEqual;


@Service
@AllArgsConstructor
@Log4j2
public class ExBetServiceImpl implements ExBetService {

	private final SapService sapService;
	private final ExBetRepository exBetRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final FetchSofaEvent fetchSofaEvent;
//	private final HistoryFetchEventService historyFetchEventService;

	@Override
	public BaseResponse getScheduledEventInPlay() {

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
	public GetExBetEventByDateWithDetails.Response getExBetEventByDateWithDetails(GetExBetEventByDateWithDetails.Request request) {
		String date = request.getDate();
		LocalDateTime localDateTime = TimeUtil.convertStringToLocalDateTime(date);

		CompletableFuture<List<ExBetCommonDto.ExBetMatchResponseDto>> exBetByDateFuture = CompletableFuture.supplyAsync(() -> exBetRepository.getExBetByDate(date));


		CompletableFuture<SfEventsResponse> sfEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					String cacheKey = "sofaEventsByDate::" + EventsByDateDTO.date;
					SfEventsResponse cachedResponse = (SfEventsResponse) redisTemplate.opsForValue().get(cacheKey);
					if (cachedResponse != null) {
						log.info("#getEventsByDate - [Found] cached sofa events for date: [{}] in [Thread: {}]", EventsByDateDTO.date, Thread.currentThread().getName());
						return cachedResponse;
					} else {
						log.info("#getEventsByDate - [Fetching] sofa events for date: [{}] in [Thread: {}]", EventsByDateDTO.date, Thread.currentThread().getName());
						SfEventsResponse sfEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SfEventsResponse.class);
						redisTemplate.opsForValue().set(cacheKey, sfEventsResponse);
						return sfEventsResponse;
					}
				});

		CompletableFuture<SfEventsResponse> sfInverseEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {

					String cacheKey = "sofaInverseEventsByDate::" + EventsByDateDTO.date;
					SfEventsResponse cachedResponse = (SfEventsResponse) redisTemplate.opsForValue().get(cacheKey);
					if (cachedResponse != null) {
						log.info("#getEventsByDate - [Found] cached sofa inverse events for date: [{}] in [Thread: {}]", EventsByDateDTO.date, Thread.currentThread().getName());
						return cachedResponse;
					} else {
						log.info("#getEventsByDate - [Fetching] sofa inverse events for date: [{}] in [Thread: {}]", EventsByDateDTO.date, Thread.currentThread().getName());
						SfEventsResponse sfEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SfEventsResponse.class);
						redisTemplate.opsForValue().set(cacheKey, sfEventsResponse);
						return sfEventsResponse;
					}
				});


		return sfEventsResponseFuture.thenCombineAsync(sfInverseEventsResponseFuture, (sfEventResponse, sfInverseEvents) -> {
					List<SfEventsResponse.EventResponse> sfEventResponses = sfEventResponse.getEvents();
					List<SfEventsResponse.EventResponse> sfInverseEventsEvents = sfInverseEvents.getEvents();
					boolean addAll = sfEventResponses.addAll(sfInverseEventsEvents);
					if (addAll) {
						return getSfEventDtoByDate(sfEventResponses, localDateTime);
					} else {
						throw new RuntimeException("Error");
					}
				})
				.thenCombine(exBetByDateFuture, (sofaDto, exBet) -> {

					List<GetExBetEventByDateWithDetails.ExBetMatchDetailsResponseDto> exBetMatches = new ArrayList<>();

					for (ExBetCommonDto.ExBetMatchResponseDto exBetMatchResponseDto : exBet) {
						SfEventsDto.EventDto eventDto = checkEightXBetMatcInEventDTO(exBetMatchResponseDto, sofaDto);
						if (Objects.nonNull(eventDto)) {
							GetExBetEventByDateWithDetails.ExBetMatchDetailsResponseDto exBetMatchDetailsResponseDto = GetExBetEventByDateWithDetails.ExBetMatchDetailsResponseDto
									.builder()
									.iid(exBetMatchResponseDto.getIid())
									.tntName(exBetMatchResponseDto.getTntName())
									.inPlay(exBetMatchResponseDto.getInPlay())
									.homeName(exBetMatchResponseDto.getHomeName())
									.awayName(exBetMatchResponseDto.getAwayName())
									.slug(exBetMatchResponseDto.getSlug())
									.kickoffTime(exBetMatchResponseDto.getKickoffTime())
//									.sofaDetail(buildSofaEvent(eventDto))
									.build();
							exBetMatches.add(exBetMatchDetailsResponseDto);
						}
					}

					return exBetMatches;
				})
				.thenApply(response -> GetExBetEventByDateWithDetails.Response
						.builder()
						.code("")
						.msg("")
						.data(GetExBetEventByDateWithDetails.ExBetDetailsResponseDto
								.builder()
								.total(response.size())
								.date(date)
								.matches(response)
								.build())
						.build())
				.whenComplete((result, throwable) -> {
					if (Objects.nonNull(throwable)) {
						log.error("#getEventsByDate - Error occurred: {}", throwable.getMessage());
					}
				})
				.join();
	}

	@Override
	public BaseResponse getEventsByDate(String date) {

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


		CompletableFuture<SfEventsResponse> sofaEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					String cacheKey = "sofaEventsByDate::" + date;
					SfEventsResponse cachedResponse = (SfEventsResponse) redisTemplate.opsForValue().get(cacheKey);
					if (cachedResponse != null) {
						log.info("#getEventsByDate - [Found] cached sofa events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						return cachedResponse;
					} else {
						log.info("#getEventsByDate - [Fetching] sofa events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						SfEventsResponse sfEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SfEventsResponse.class);
						redisTemplate.opsForValue().set(cacheKey, sfEventsResponse);
						return sfEventsResponse;
					}
				});

		CompletableFuture<SfEventsResponse> sofaInverseEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {

					String cacheKey = "sofaInverseEventsByDate::" + date;
					SfEventsResponse cachedResponse = (SfEventsResponse) redisTemplate.opsForValue().get(cacheKey);
					if (cachedResponse != null) {
						log.info("#getEventsByDate - [Found] cached sofa inverse events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						return cachedResponse;
					} else {
						log.info("#getEventsByDate - [Fetching] sofa inverse events for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
						SfEventsResponse sfEventsResponse = sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SfEventsResponse.class);
						redisTemplate.opsForValue().set(cacheKey, sfEventsResponse);
						return sfEventsResponse;
					}
				});

		return sofaEventsResponseFuture.thenCombineAsync(sofaInverseEventsResponseFuture, (sofaEventsResponse, sofaInverseEventsResponse) -> {
					log.info("#getEventsByDate - [Merging] {{sofa events}} with {{inverse sofa events}} for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					List<SfEventsResponse.EventResponse> eventsResponse = sofaEventsResponse.getEvents();
					List<SfEventsResponse.EventResponse> eventsInverseResponse = sofaInverseEventsResponse.getEvents();
					eventsResponse.addAll(eventsInverseResponse);
					getSfEventDtoByDate(eventsResponse, TimeUtil.convertStringToLocalDateTime(date));
					return getSfEventDtoByDate(eventsResponse, TimeUtil.convertStringToLocalDateTime(date));
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
						int totalMatchesWithId = 0;
						int totalMatchesWitNoId = 0;

						for (EightXBetTournamentDTO eightXBetTournamentDTO : eightXBetTournamentDTOEntry.getValue()) {

							List<EightXBetMatchDTO> eightXBetMatchDtoWithId = new ArrayList<>();
							List<EightXBetMatchDTO> eightXBetMatchDtoWithNoId = new ArrayList<>();

							for (EightXBetMatchDTO eightXBetMatchDTO : eightXBetTournamentDTO.getMatches()) {
								SfEventsDto.EventDto eventDTO = null;
								if (!Objects.isNull(eventDTO)) {
									idsForSofaToFetch.add(eventDTO.getHomeDetails().getIdTeam());
									idsForSofaToFetch.add(eventDTO.getAwayDetails().getIdTeam());
									SofaEvent sofaEvent = buildSofaEvent(eventDTO);
									eightXBetMatchDTO.setSofaDetail(sofaEvent);
									eightXBetMatchDtoWithId.add(eightXBetMatchDTO);
									totalMatchesWithId++;
								} else {
									eightXBetMatchDtoWithNoId.add(eightXBetMatchDTO);
									totalMatchesWitNoId++;
								}
							}


							if (!eightXBetMatchDtoWithId.isEmpty()) {
								EightXBetTournamentDTO eightXBetTournamentDTOFirst = EightXBetTournamentDTO
										.builder()
										.tntName(eightXBetTournamentDTO.getTntName())
										.count(eightXBetMatchDtoWithId.size())
										.matches(eightXBetMatchDtoWithId)
										.build();
								tournamentDtoWithId.add(eightXBetTournamentDTOFirst);
							}

							if (!eightXBetMatchDtoWithNoId.isEmpty()) {
								EightXBetTournamentDTO eightXBetTournamentDTOSecond = EightXBetTournamentDTO
										.builder()
										.tntName(eightXBetTournamentDTO.getTntName())
										.count(eightXBetMatchDtoWithNoId.size())
										.matches(eightXBetMatchDtoWithNoId)
										.build();
								tournamentDTOWithNoId.add(eightXBetTournamentDTOSecond);
							}
						}

						processIdsForFetchHistoryEvent(idsForSofaToFetch);


						EventsByDateDTO.TournamentDTO tournamentFirst = EventsByDateDTO.TournamentDTO.builder()
								.eightXBetTournamentDto(tournamentDtoWithId)
								.matchSize(totalMatchesWithId)
								.tntSize(tournamentDtoWithId.size())
								.build();

						EventsByDateDTO.TournamentDTO tournamentSecond = EventsByDateDTO.TournamentDTO.builder()
								.eightXBetTournamentDto(tournamentDTOWithNoId)
								.matchSize(totalMatchesWitNoId)
								.tntSize(tournamentDTOWithNoId.size())
								.build();

						return EventsByDateDTO
								.Response
								.builder()
								.totalMatches(eightXBetTournamentDTOEntry.getKey())
								.totalTournaments(eightXBetTournamentDTOEntry.getValue().size())
								.tournamentsWithId(tournamentFirst)
								.tournamentsWithNoId(tournamentSecond)
								.build();
					}
					return null;
				})
				.thenApply(response -> BaseResponse
						.builder()
						.code("")
						.msg("")
//						.data(response)
						.build())
				.whenComplete((result, throwable) -> {
					if (Objects.nonNull(throwable)) {
						log.error("#getEventsByDate - Error occurred: {}", throwable.getMessage());
					}
				})
				.join();
	}

	@Override
	public ImportExBetFromFile.Response getDataFile(MultipartFile file) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			InputStream inputStream = file.getInputStream();
			ExBetResponse exBetResponse = objectMapper.readValue(inputStream, ExBetResponse.class);
			ExBetResponse.Data data = exBetResponse.getData();
			List<ExBetTournamentResponse> tournaments = data.getTournaments();


			ImportExBetFromFile.ExBetResponseDto exBetResponseDto = exBetRepository.saveExBetEntity(tournaments);


			return ImportExBetFromFile.Response.builder()
					.data(exBetResponseDto)
					.build();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public GetExBetEventByDate.Response getExBetEventByDate(GetExBetEventByDate.Request request) {
		List<ExBetCommonDto.ExBetMatchResponseDto> exBetMatchResponseDtos = exBetRepository.getExBetByDate(request.getDate());
		return GetExBetEventByDate.Response.builder()
				.data(GetExBetEventByDate.ExBetResponseDto.builder()
						.total(exBetMatchResponseDtos.size())
						.date(request.getDate())
						.matches(exBetMatchResponseDtos)
						.build())
				.build();
	}

	private void processIdsForFetchHistoryEvent(List<Integer> ids) {
//		CompletableFuture.runAsync(() -> {
//			log.info("#processIdsForFetchHistoryEvent - [Processing] ids for fetch history event in [Thread: {}]", Thread.currentThread().getName());
//			List<Integer> historyFetchEventFetched = historyFetchEventService.getHistoryFetchEventWithStatus(FetchStatus.FETCHED);
//			List<Integer> idsToFetch = new ArrayList<>();
//			for (Integer id : ids) {
//				if (!historyFetchEventFetched.contains(id)) {
//					idsToFetch.add(id);
//				}
//			}
//			if (!idsToFetch.isEmpty()) {
//				historyFetchEventService.saveHistoryEventWithIds(idsToFetch);
//			}
//		});
	}


	private SofaEvent buildSofaEvent(SfEventsDto.@NotNull EventDto eventDTO) {
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

	private List<SfEventsDto.EventDto> getSfEventDtoByDate(List<SfEventsResponse.EventResponse> eventResponses,
														   LocalDateTime requestDate) {
		List<SfEventsDto.EventDto> sofaEventDto = new ArrayList<>();
		for (SfEventsResponse.EventResponse eventResponse : eventResponses) {
			LocalDateTime responseDate = TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp());
			if (responseDate.getDayOfMonth() == requestDate.getDayOfMonth() &&
					responseDate.getMonth() == requestDate.getMonth() &&
					responseDate.getYear() == requestDate.getYear()) {
				SfEventsDto.EventDto eventDTO = populatedToEventDTO(eventResponse);
				sofaEventDto.add(eventDTO);
			}
		}
		return sofaEventDto;
	}

	public SfEventsDto.EventDto populatedToEventDTO(SfEventsResponse.EventResponse eventResponse) {

		SofaCommonResponse.Score homeScoreResponse = eventResponse.getHomeScore();
		SofaCommonResponse.Score eventResponseAwayScore = eventResponse.getAwayScore();

		return SfEventsDto.EventDto.builder()
				.id(eventResponse.getId())
				.tntName(eventResponse.getTournament().getName())
				.seasonName(Objects.isNull(eventResponse.getSeason()) ? null : eventResponse.getSeason().getName())
				.round(Objects.isNull(eventResponse.getRoundInfo()) ? null : eventResponse.getRoundInfo().getRound())
				.status(Objects.isNull(eventResponse.getStatus()) ? null : eventResponse.getStatus().getDescription())
				.homeDetails(SfEventsDto.TeamDetails.builder()
						.idTeam(eventResponse.getHomeTeam().getId())
						.name(eventResponse.getHomeTeam().getName())
						.build())
				.awayDetails(SfEventsDto.TeamDetails.builder()
						.idTeam(eventResponse.getAwayTeam().getId())
						.name(eventResponse.getAwayTeam().getName())
						.build())
				.kickOffMatch(TimeUtil.convertUnixTimestampToLocalDateTime(eventResponse.getStartTimestamp()))
				.build();
	}


	private @Nullable SfEventsDto.EventDto checkEightXBetMatcInEventDTO(ExBetCommonDto.ExBetMatchResponseDto eightXBetMatchDTO, List<SfEventsDto.EventDto> eventDtos) {

		String eightXBetNameHome = eightXBetMatchDTO.getHomeName();
		String eightXBetAwayName = eightXBetMatchDTO.getAwayName();
		LocalDateTime kickoffTime = eightXBetMatchDTO.getKickoffTime();

		boolean isEqualNameFirst;
		boolean isEqualNameSecond;

		for (SfEventsDto.EventDto eventDTO : eventDtos) {
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
