package org.data.eightBet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.data.common.exception.ApiException;
import org.data.eightBet.dto.EightXBetCommonResponse;
import org.data.eightBet.dto.EventsByDateDTO;
import org.data.eightBet.dto.EightXBetEventsResponse;
import org.data.eightBet.dto.EventInPlayDTO;
import org.data.eightBet.dto.EightXBetEventsResponse.EightXBetTournamentResponse;
import org.data.eightBet.repository.EightXBetRepository;
import org.data.persistent.entity.EventsEightXBetEntity;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.SofaCommonResponse;
import org.data.sofa.dto.SofaEventsDTO;
import org.data.sofa.dto.SofaEventsResponse;
import org.data.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
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


@Service
@AllArgsConstructor
@Log4j2
public class EightXBetServiceImpl implements EightXBetService {

	private final SapService sapService;
	private final EightXBetRepository eightXBetRepository;

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
//					log.info("#getEventsByDate - fetching [EightXBet events] for date: [{}] in [Thread: {}]", TimeUtil.convertIntoXet(date), Thread.currentThread().getName());
//					return sapService.restEightXBetGet(EventsByDateDTO.GET_EVENTS_BY_DATE,
//							EventsByDateDTO.queryParams(),
//							ScheduledEventEightXBetResponse.class);
				});


		CompletableFuture<SofaEventsResponse> sofaEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getEventsByDate - fetching [Sofa events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SofaEventsResponse.class);
				});

		CompletableFuture<SofaEventsResponse> sofaInverseEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getEventsByDate - fetching [Inverse events events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SofaEventsResponse.class);
				});

		return sofaEventsResponseFuture.thenCombineAsync(sofaInverseEventsResponseFuture, (sofaEventsResponse, sofaInverseEventsResponse) -> {
					List<SofaEventsDTO.EventDTO> eventDTOS = new ArrayList<>();
					List<SofaEventsResponse.EventResponse> eventsResponse = sofaEventsResponse.getEvents();
					List<SofaEventsResponse.EventResponse> eventsInverseResponse = sofaInverseEventsResponse.getEvents();
					eventsResponse.addAll(eventsInverseResponse);
					getEventDTOByDate(eventsResponse, eventDTOS, TimeUtil.convertStringToLocalDateTime(date));
					return eventDTOS;
				})
				.thenCombine(eightXBetEventResponseFuture, (eventDTOS, eightXBetEventResponse) -> {
					Data data = eightXBetEventResponse.getData();
					EventsByDateDTO.Response response = null;
					if (!data.getTournaments().isEmpty()) {

						Map<Integer, List<EightXBetTournamentDTO>> eightXBetTournamentDTOMap = convertToEightXBetTournamentDTO(data.getTournaments());
						Map.Entry<Integer, List<EightXBetTournamentDTO>> eightXBetTournamentDTOEntry = eightXBetTournamentDTOMap
								.entrySet()
								.iterator()
								.next();

						for (EightXBetTournamentDTO eightXBetTournamentDTO : eightXBetTournamentDTOEntry.getValue()) {
							for (EightXBetMatchDTO eightXBetMatchDTO : eightXBetTournamentDTO.getMatches()) {
								processSofaEvent(eightXBetMatchDTO, eventDTOS);
							}
						}

						return EventsByDateDTO
								.Response
								.builder()
								.eightXBetTournamentDto(eightXBetTournamentDTOEntry.getValue())
								.tntSize(eightXBetTournamentDTOEntry.getValue().size())
								.matchSize(eightXBetTournamentDTOEntry.getKey())
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

	/**
	 * Convert EightXBetTournamentResponse to EightXBetTournamentDTO
	 *
	 * @param eightXBetTournamentResponses List of EightXBetTournamentResponse
	 * @return Map<Integer, List < EightXBetTournamentDTO>>
	 */
	private @NotNull @Unmodifiable Map<Integer, List<EightXBetTournamentDTO>> convertToEightXBetTournamentDTO(@NotNull List<EightXBetTournamentResponse> eightXBetTournamentResponses) {
		List<EightXBetTournamentDTO> eightXBetTournamentDTOS = new ArrayList<>();
		int totalMatches = 0;

		for (EightXBetTournamentResponse eightXBetTournamentResponse : eightXBetTournamentResponses) {

			String name = eightXBetTournamentResponse.getName();
			List<EightXBetMatchDTO> eightXBetMatchDTOS = new ArrayList<>();

			for (EightXBetCommonResponse.EightXBetMatchResponse eightXBetMatchRsp : eightXBetTournamentResponse.getMatches()) {

				EightXBetMatchDTO eightXBetMatchDTO = buildEightXBetMatchDTO(eightXBetMatchRsp);
				eightXBetMatchDTOS.add(eightXBetMatchDTO);
				totalMatches++;
			}

			EightXBetTournamentDTO eightXBetTournamentDTO = EightXBetTournamentDTO
					.builder()
					.tntName(name)
					.count(eightXBetMatchDTOS.size())
					.matches(eightXBetMatchDTOS)
					.build();

			eightXBetTournamentDTOS.add(eightXBetTournamentDTO);

		}
		return Map.of(totalMatches, eightXBetTournamentDTOS);
	}


	public void getEventDTOByDate(List<SofaEventsResponse.EventResponse> eventResponses,
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

	private String preHandleNameTeam(String name) {
		return name.replaceAll("[^\\p{ASCII}]", "")
				.toLowerCase();
	}


	private void processSofaEvent(EightXBetMatchDTO eightXBetMatchDTO, List<SofaEventsDTO.EventDTO> eventDTOS) {

		String eightXBetNameHome = preHandleNameTeam(eightXBetMatchDTO.getHomeName());
		String eightXBetAwayName = preHandleNameTeam(eightXBetMatchDTO.getAwayName());
		LocalDateTime kickoffTime = eightXBetMatchDTO.getKickoffTime();

		for (SofaEventsDTO.EventDTO eventDTO : eventDTOS) {
			String sofaNameHome = preHandleNameTeam(eventDTO.getHomeDetails().getName());
			String sofaAwayName = preHandleNameTeam(eventDTO.getAwayDetails().getName());
			LocalDateTime kickOffMatch = eventDTO.getKickOffMatch();

			if (Objects.equals(eightXBetNameHome, sofaNameHome) || Objects.equals(eightXBetAwayName, sofaNameHome)) {

				if (TimeUtil.isEqual(kickoffTime, kickOffMatch)) {
					Team firstTeam = Team.builder()
							.name(sofaNameHome)
							.build();

					Team secondTeam = Team.builder()
							.name(sofaAwayName)
							.build();

					SofaEvent sofaEvent = SofaEvent.builder()
							.firstTeam(firstTeam)
							.secondTeam(secondTeam)
							.build();

					eightXBetMatchDTO.setSofaDetail(sofaEvent);
				}
			}
		}
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

	private EventsByDateDTO.Response populateScheduledEventByDateResponseToDTOToDisplay(List<EightXBetTournamentResponse> tournamentResponses, String date) {

		LocalDateTime localDateTimeRequest = TimeUtil.convertStringToLocalDateTime(date);
		List<EightXBetTournamentDTO> eightXBetTournamentDTOS = new ArrayList<>();
		int mtSize = 0;

		for (EightXBetTournamentResponse tournamentResponse : tournamentResponses) {

			List<EightXBetCommonResponse.EightXBetMatchResponse> matchesResponse = tournamentResponse.getMatches();
			List<EightXBetMatchDTO> eightXBetMatchDTOS = new ArrayList<>();
			String tournamentResponseName = tournamentResponse.getName();

			for (EightXBetCommonResponse.EightXBetMatchResponse matchResponse : matchesResponse) {
				long kickoffTimeResponse = matchResponse.getKickoffTime();
				LocalDateTime localDateTimeResponse = TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTimeResponse);
				if (localDateTimeRequest.getDayOfMonth() == localDateTimeResponse.getDayOfMonth()) {
					EightXBetMatchDTO eightXBetMatchDTO = buildEightXBetMatchDTO(matchResponse);
					mtSize++;
					eightXBetMatchDTOS.add(eightXBetMatchDTO);
				}
			}

			if (!eightXBetMatchDTOS.isEmpty()) {
				EightXBetTournamentDTO eightXBetTournamentDTO = EightXBetTournamentDTO
						.builder()
						.tntName(tournamentResponseName)
						.matches(eightXBetMatchDTOS)
						.count(eightXBetMatchDTOS.size())
						.build();
				eightXBetTournamentDTOS.add(eightXBetTournamentDTO);
			}
		}
		return EventsByDateDTO
				.Response
				.builder()
				.eightXBetTournamentDto(eightXBetTournamentDTOS)
				.tntSize(eightXBetTournamentDTOS.size())
				.matchSize(mtSize)
				.build();
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
