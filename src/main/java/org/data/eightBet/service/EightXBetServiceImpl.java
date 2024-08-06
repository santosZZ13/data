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
import java.text.Normalizer;
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

	private static final Map<String, String> TRANSLITERATION_MAP = new HashMap<>();

	static {
		TRANSLITERATION_MAP.put("kobenhavn", "copenhagen");
	}

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
				Team firstTeam = Team.builder()
						.name(eventDTO.getHomeDetails().getName())
						.build();
				Team secondTeam = Team.builder()
						.name(eventDTO.getAwayDetails().getName())
						.build();
				SofaEvent sofaEvent = SofaEvent.builder()
						.firstTeam(firstTeam)
						.secondTeam(secondTeam)
						.build();
				eightXBetMatchDTO.setSofaDetail(sofaEvent);
				break;
			}
		}
	}

	public String normalizeTeamName(String name) {
		// Convert to lower case
		name = name.toLowerCase();
		// Remove special characters and accents
		name = Normalizer.normalize(name, Normalizer.Form.NFD);
		name = name.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
		name = name.replaceAll("ø", "o");
		name = name.replaceAll("å", "a");
		// Remove common suffixes
		name = name.replaceAll("\\b(fc|bk|ksv|eh|ff|il|tf|us|de|sk|fk)\\b", "");
		// Remove extra spaces
		name = name.replaceAll("\\s+", " ").trim();
		return name;
	}

	private String applyTransliteration(String name) {
		String[] words = name.split(" ");
		StringBuilder transliteratedName = new StringBuilder();

		for (String word : words) {
			if (TRANSLITERATION_MAP.containsKey(word)) {
				transliteratedName.append(TRANSLITERATION_MAP.get(word)).append(" ");
			} else {
				transliteratedName.append(word).append(" ");
			}
		}

		return transliteratedName.toString().trim();
	}


	private boolean areTeamNamesEqual(String nameFirst, String nameSecond) {
		// Stage 1:
		String appliedTransliterationFirst = applyTransliteration(normalizeTeamName(nameFirst));
		String appliedTransliterationSecond = applyTransliteration(normalizeTeamName(nameSecond));
		if (appliedTransliterationFirst.equals(appliedTransliterationSecond)) {
			return true;
		}

		// Stage 2: "eif" and "Ekenas IF"
		List<String> splitTeamFirst = Arrays.stream(appliedTransliterationFirst.split(" ")).toList();
		List<String> splitTeamSecond = Arrays.stream(appliedTransliterationSecond.split(" ")).toList();

		if (splitTeamFirst.size() > splitTeamSecond.size()) {
			String first = splitTeamFirst.get(0);
			String second = splitTeamFirst.get(1);
			String fullName;
			if (first.length() > second.length()) {
				fullName = first.charAt(0) + second;
			} else {
				fullName = second.charAt(0) + first;
			}

			if (fullName.equals(splitTeamSecond.get(0))) {
				return true;
			}
		} else if (splitTeamFirst.size() < splitTeamSecond.size()) {

			String first = splitTeamSecond.get(0);
			String second = splitTeamSecond.get(1);
			String fullName;
			if (first.length() > second.length()) {
				fullName = first.charAt(0) + second;
			} else {
				fullName = second.charAt(0) + first;
			}

			if (fullName.equals(splitTeamFirst.get(0))) {
				return true;
			}
		}

		// Stage 3: Feyenoord Rotterdam vs feyenoord
		List<String> appliedFirst = Arrays.stream(appliedTransliterationFirst.split(" ")).toList();
		List<String> appliedSecond = Arrays.stream(appliedTransliterationSecond.split(" ")).toList();
		for (String ele : appliedFirst) {
			if (appliedSecond.contains(ele)) {
				return true;
			}
		}
		return false;
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
