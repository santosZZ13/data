package org.data.eightBet.service;

import lombok.AllArgsConstructor;
import org.data.common.exception.ApiException;
import org.data.eightBet.dto.EventsByDateDTO;
import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse;
import org.data.eightBet.dto.EventInPlayDTO;
import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.MatchResponse;
import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.TournamentResponse;
import org.data.eightBet.repository.EightXBetRepository;
import org.data.persistent.entity.EventsEightXBetEntity;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.data.eightBet.dto.EventDTO.*;
import static org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.*;


@Service
@AllArgsConstructor
public class EightXBetServiceImpl implements EightXBetService {

	private final SapService sapService;
	private final EightXBetRepository eightXBetRepository;

	@Override
	public GenericResponseWrapper getScheduledEventInPlay() {

		ScheduledEventInPlayEightXBetResponse scheduledEventInPlayEightXBetResponse = sapService.restEightXBetGet(EventInPlayDTO.EIGHT_X_BET,
				EventInPlayDTO.queryParams(),
				ScheduledEventInPlayEightXBetResponse.class);

		Data data = scheduledEventInPlayEightXBetResponse.getData();

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
		ScheduledEventInPlayEightXBetResponse scheduledEventInPlayEightXBetResponse = sapService.restEightXBetGet(EventsByDateDTO.GET_EVENTS_BY_DATE,
				EventsByDateDTO.queryParams(),
				ScheduledEventInPlayEightXBetResponse.class);

		Data data = scheduledEventInPlayEightXBetResponse.getData();

		if (Objects.isNull(data.getTournaments())) {
			throw new ApiException("Errors", "", "No data found for scheduled events in play");
		}

		EventsByDateDTO.Response response = populateScheduledEventByDateResponseToDTO(data.getTournaments(), date);

		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(response)
				.build();
	}

	private EventsByDateDTO.Response populateScheduledEventByDateResponseToDTO(List<TournamentResponse> tournamentResponses, String date) {


		// date: yyyy-MM-dd

		List<TournamentDTO> tournamentDTOS = new ArrayList<>();


		for (TournamentResponse tournamentResponse : tournamentResponses) {


			List<MatchResponse> matchesResponse = tournamentResponse.getMatches();

			for (MatchResponse matchResponse : matchesResponse) {

				long kickoffTimeResponse = matchResponse.getKickoffTime();
				LocalDateTime localDateTimeResponse = TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTimeResponse);
			}
		}
		return null;
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
				.homeName(homeResponse.getName())
				.awayName(awayResponse.getName())
				.slug(matchResponse.getName())
				.inPlay(matchResponse.getInplay())
				.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTime))
				.build();
	}


}
