package org.data.eightBet.service;

import lombok.AllArgsConstructor;
import org.data.common.exception.ApiException;
import org.data.eightBet.dto.EventDTO;
import org.data.eightBet.dto.ScheduledEventInPlayConfigDTO;
import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse;
import org.data.eightBet.dto.EventInPlayDTO;
import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.MatchResponse;
import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.TournamentResponse;
import org.data.eightBet.repository.EightXBetRepository;
import org.data.persistent.entity.ScheduledEventsEightXBetEntity;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.util.TimeUtil;
import org.springframework.stereotype.Service;

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

		ScheduledEventInPlayEightXBetResponse scheduledEventInPlayEightXBetResponse = sapService.restEightXBetGet(ScheduledEventInPlayConfigDTO.EIGHT_X_BET,
				ScheduledEventInPlayConfigDTO.queryParams(),
				ScheduledEventInPlayEightXBetResponse.class);

		Data data = scheduledEventInPlayEightXBetResponse.getData();

		if (Objects.isNull(data.getTournaments())) {
			throw new ApiException("Errors", "", "No data found for scheduled events in play");
		}

		EventInPlayDTO.Response response = populateScheduledEventInPlayResponseToDTO(data.getTournaments());

		if (!Objects.equals(response.getTntSize(), 0)) {
			List<ScheduledEventsEightXBetEntity> scheduledEventsEightXBetEntities = eightXBetRepository.saveTournamentResponse(data.getTournaments());
		}

		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(response)
				.build();

	}

	public EventInPlayDTO.Response populateScheduledEventInPlayResponseToDTO(List<TournamentResponse> tournamentResponses) {

		List<TournamentDTO> tournaments = new ArrayList<>();
		int matchSize = 0;

		for (int i = 0; i < tournamentResponses.size(); i++) {

			TournamentResponse tournamentResponse = tournamentResponses.get(i);
			List<MatchResponse> matchResponses = tournamentResponse.getMatches();

			String name = tournamentResponse.getName();
			List<MatchDTO> matches = new ArrayList<>();

			for (int i1 = 0; i1 < matchResponses.size(); i1++) {
				matchSize++;

				MatchResponse matchResponse = matchResponses.get(i1);

				TeamResponse homeResponse = matchResponse.getHome();
				TeamResponse awayResponse = matchResponse.getAway();
				long kickoffTime = matchResponse.getKickoffTime();

				MatchDTO matchDTO = MatchDTO.builder()
						.homeName(homeResponse.getName())
						.awayName(awayResponse.getName())
						.slug(matchResponse.getName())
						.inPlay(matchResponse.getInplay())
						.kickoffTime(TimeUtil.convertUnixTimestampToLocalDateTime(kickoffTime))
						.build();

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


}
