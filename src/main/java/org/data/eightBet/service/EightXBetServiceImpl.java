package org.data.eightBet.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.common.exception.ApiException;
import org.data.eightBet.dto.EventsByDateDTO;
import org.data.eightBet.dto.ScheduledEventEightXBetResponse;
import org.data.eightBet.dto.EventInPlayDTO;
import org.data.eightBet.dto.ScheduledEventEightXBetResponse.MatchResponse;
import org.data.eightBet.dto.ScheduledEventEightXBetResponse.TournamentResponse;
import org.data.eightBet.repository.EightXBetRepository;
import org.data.persistent.entity.EventsEightXBetEntity;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.ScheduledEventsCommonResponse;
import org.data.sofa.dto.SofaScheduledEventByDateDTO;
import org.data.sofa.dto.SofaScheduledEventsResponse;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.data.eightBet.dto.EventDTO.*;
import static org.data.eightBet.dto.ScheduledEventEightXBetResponse.*;
import static org.data.sofa.dto.SofaScheduledEventByDateDTO.SCHEDULED_EVENTS;
import static org.data.sofa.dto.SofaScheduledEventByDateDTO.SCHEDULED_EVENTS_INVERSE;


@Service
@AllArgsConstructor
@Log4j2
public class EightXBetServiceImpl implements EightXBetService {

	private final SapService sapService;
	private final EightXBetRepository eightXBetRepository;

	@Override
	public GenericResponseWrapper getScheduledEventInPlay() {

		ScheduledEventEightXBetResponse scheduledEventEightXBetResponse = sapService.restEightXBetGet(EventInPlayDTO.EIGHT_X_BET,
				EventInPlayDTO.queryParams(),
				ScheduledEventEightXBetResponse.class);

		Data data = scheduledEventEightXBetResponse.getData();

		if (Objects.isNull(data.getTournaments())) {
			throw new ApiException("Errors", "", "No data found for scheduled events in play");
		}

		EventInPlayDTO.Response response = populateScheduledEventInPlayResponseToDTO(data.getTournaments());

		if (!Objects.equals(response.getTntSize(), 0)) {
			List<EventsEightXBetEntity> scheduledEventsEightXBetEntities = eightXBetRepository.saveTournamentResponse(data.getTournaments());
		}

		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(response)
				.build();

	}

	@Override
	public GenericResponseWrapper getEventsByDate(String date) {

		CompletableFuture<ScheduledEventEightXBetResponse> EightXBetEventResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getEventsByDate - fetching [EightXBet events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return sapService.restEightXBetGet(EventsByDateDTO.GET_EVENTS_BY_DATE,
							EventsByDateDTO.queryParams(),
							ScheduledEventEightXBetResponse.class);
				});


		CompletableFuture<SofaScheduledEventsResponse> sofaEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getEventsByDate - fetching [Sofa events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date, SofaScheduledEventsResponse.class);
				});

		CompletableFuture<SofaScheduledEventsResponse> sofaInverseEventsResponseFuture =
				CompletableFuture.supplyAsync(() -> {
					log.info("#getEventsByDate - fetching [Inverse events events] for date: [{}] in [Thread: {}]", date, Thread.currentThread().getName());
					return sapService.restSofaScoreGet(SCHEDULED_EVENTS + date + SCHEDULED_EVENTS_INVERSE, SofaScheduledEventsResponse.class);
				});

		// Combine sofaEventsResponseFuture and sofaInverseEventsResponseFuture. After that, combine the result with EightXBetEventResponseFuture
		 sofaEventsResponseFuture.thenCombineAsync(sofaInverseEventsResponseFuture, (sofaEventsResponse, sofaInverseEventsResponse) -> {

			 List<SofaScheduledEventsResponse.EventResponse> events = sofaEventsResponse.getEvents();
			 List<SofaScheduledEventsResponse.EventResponse> inverseEvents = sofaInverseEventsResponse.getEvents();
			 events.addAll(inverseEvents);


			 for (SofaScheduledEventsResponse.EventResponse event : events) {


			 }




			 return sofaEventsResponse;
		})





//		Data data = scheduledEventInPlayEightXBetResponseCompletableFuture.getData();
//		EventsByDateDTO.Response response = populateScheduledEventByDateResponseToDTOToDisplay(data.getTournaments(), date);


//		if (!data.getTournaments().isEmpty()) {
//			eightXBetRepository.saveTournamentResponse(data.getTournaments());
//		}


		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(response)
				.build();

	}



	@Override
	@Transactional
	public GenericResponseWrapper fetchEvents() {

//		eightXBetRepository.updateInplayEvent();

		ScheduledEventEightXBetResponse scheduledEventEightXBetResponse = sapService.restEightXBetGet(EventsByDateDTO.GET_EVENTS_BY_DATE,
				EventsByDateDTO.queryParams(),
				ScheduledEventEightXBetResponse.class);

		Data data = scheduledEventEightXBetResponse.getData();
		List<TournamentResponse> tournaments = data.getTournaments();

		List<Integer> iidEventEntities = new ArrayList<>();
		Map<TournamentResponse, MatchResponse> tournamentMatchResponseMap = new LinkedHashMap<>();

		if (!tournaments.isEmpty()) {
			List<EventsEightXBetEntity> eventsEntities = eightXBetRepository.getAllEventsEntity();
			eventsEntities.forEach(eventEntity -> iidEventEntities.add(eventEntity.getIId()));


			tournaments.forEach(tournament -> {
				List<MatchResponse> matches = tournament.getMatches();
				matches.forEach(match -> {
					if (!iidEventEntities.contains(match.getIid())) {
						tournamentMatchResponseMap.put(tournament, match);
					}
				});
			});

			eightXBetRepository.saveMatchesMap(tournamentMatchResponseMap);
		}

		Collection<MatchResponse> matches = tournamentMatchResponseMap.values();
		List<MatchDTO> matchDTOS = new ArrayList<>();
		for (MatchResponse match : matches) {
			matchDTOS.add(buildMatchDTO(match));
		}


		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(matchDTOS)
				.build();
	}

	private EventsByDateDTO.Response populateScheduledEventByDateResponseToDTOToDisplay(List<TournamentResponse> tournamentResponses, String date) {

		LocalDateTime localDateTimeRequest = TimeUtil.convertStringToLocalDateTime(date);
		List<TournamentDTO> tournamentDTOS = new ArrayList<>();
		int mtSize = 0;

		for (TournamentResponse tournamentResponse : tournamentResponses) {

			List<MatchResponse> matchesResponse = tournamentResponse.getMatches();
			List<MatchDTO> matchDTOS = new ArrayList<>();
			String tournamentResponseName = tournamentResponse.getName();


			for (MatchResponse matchResponse : matchesResponse) {
				long kickoffTimeResponse = matchResponse.getKickoffTime();
				LocalDateTime localDateTimeResponse = TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTimeResponse);
				if (localDateTimeRequest.getDayOfMonth() == localDateTimeResponse.getDayOfMonth()) {
					MatchDTO matchDTO = buildMatchDTO(matchResponse);
					mtSize++;
					matchDTOS.add(matchDTO);
				}

			}

			if (!matchDTOS.isEmpty()) {
				TournamentDTO tournamentDTO = TournamentDTO
						.builder()
						.tntName(tournamentResponseName)
						.matches(matchDTOS)
						.count(matchDTOS.size())
						.build();
				tournamentDTOS.add(tournamentDTO);
			}
		}
		return EventsByDateDTO
				.Response
				.builder()
				.tournamentDto(tournamentDTOS)
				.tntSize(tournamentDTOS.size())
				.matchSize(mtSize)
				.build();
	}


	public EventInPlayDTO.Response populateScheduledEventInPlayResponseToDTO(List<TournamentResponse> tournamentResponses) {

		List<TournamentDTO> tournaments = new ArrayList<>();
		int matchSize = 0;

		for (TournamentResponse tournamentResponse : tournamentResponses) {
			List<MatchResponse> matchResponses = tournamentResponse.getMatches();
			String name = tournamentResponse.getName();
			List<MatchDTO> matches = new ArrayList<>();

			for (MatchResponse matchResponse : matchResponses) {
				matchSize++;
				MatchDTO matchDTO = buildMatchDTO(matchResponse);
				matches.add(matchDTO);
			}

			TournamentDTO tournamentDTO = TournamentDTO
					.builder()
					.tntName(name)
					.matches(matches)
					.build();

			tournaments.add(tournamentDTO);

		}

		return EventInPlayDTO
				.Response
				.builder()
				.tournamentDto(tournaments)
				.tntSize(tournamentResponses.size())
				.matchSize(matchSize)
				.build();

	}


	private MatchDTO buildMatchDTO(MatchResponse matchResponse) {
		TeamResponse homeResponse = matchResponse.getHome();
		TeamResponse awayResponse = matchResponse.getAway();
		long kickoffTime = matchResponse.getKickoffTime();
		return MatchDTO.builder()
				.iid(matchResponse.getIid())
				.homeName(homeResponse.getName())
				.awayName(awayResponse.getName())
				.slug(matchResponse.getName())
				.inPlay(matchResponse.getInplay())
				.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTime))
				.build();
	}


}
