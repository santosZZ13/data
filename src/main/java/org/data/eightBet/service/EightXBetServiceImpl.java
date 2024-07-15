package org.data.eightBet.service;

import lombok.AllArgsConstructor;
import org.data.eightBet.dto.ScheduledEventInPlayConfigDTO;
import org.data.eightBet.dto.ScheduledEventInPlayDTO.TournamentDTO;
import org.data.eightBet.dto.ScheduledEventInPlayResponse;
import org.data.eightBet.dto.ScheduledEventInPlayDTO;
import org.data.eightBet.dto.ScheduledEventInPlayResponse.MatchResponse;
import org.data.eightBet.dto.ScheduledEventInPlayResponse.TournamentResponse;
import org.data.eightBet.repository.EightXBetRepository;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.tournament.util.TimeUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.data.eightBet.dto.ScheduledEventInPlayDTO.*;
import static org.data.eightBet.dto.ScheduledEventInPlayResponse.*;


@Service
@AllArgsConstructor
public class EightXBetServiceImpl implements EightXBetService {

	private final SapService sapService;
	private final EightXBetRepository eightXBetRepository;

	@Override
	public GenericResponseWrapper getScheduledEventInPlay() {

		try {

			ScheduledEventInPlayResponse scheduledEventInPlayResponse = sapService.restEightXBetGet(ScheduledEventInPlayConfigDTO.EIGHT_X_BET,
					ScheduledEventInPlayConfigDTO.queryParam(),
					ScheduledEventInPlayResponse.class);

			Data data = scheduledEventInPlayResponse.getData();

			if (Objects.isNull(data.getTournaments())) {
				throw new Exception("Can get data from remote");
			}

			ScheduledEventInPlayDTO scheduledEventInPlayDTO = populateScheduledEventInPlayResponseToDTO(data);

			return GenericResponseWrapper
					.builder()
					.code("")
					.msg("")
					.data(scheduledEventInPlayDTO)
					.build();

		} catch (Exception ex) {
			System.out.println(ex);
		}

		return null;
	}

	public ScheduledEventInPlayDTO populateScheduledEventInPlayResponseToDTO(Data data) {

		List<TournamentResponse> tournamentResponses = data.getTournaments();
		List<TournamentDTO> tournaments = new ArrayList<>();
		int matchSize = 0;

		for (int i = 0; i < tournamentResponses.size(); i++) {

			TournamentResponse tournamentResponse = tournamentResponses.get(i);
			List<MatchResponse> matchResponses = tournamentResponse.getMatches();

			String name = tournamentResponse.getName();
			List<MatchDTO> matches = new ArrayList<>();

			for (int i1 = 0; i1 < matchResponses.size(); i1++) {
				matchSize++;

				MatchResponse matchResponse = matchResponses.get(i);

				TeamResponse homeResponse = matchResponse.getHome();
				TeamResponse awayResponse = matchResponse.getAway();
				long kickoffTime = matchResponse.getKickoffTime();

				MatchDTO matchDTO = MatchDTO.builder()
						.homeName(homeResponse.getName())
						.awayName(awayResponse.getName())
						.slug(matchResponse.getName())
						.inPlay(matchResponse.isInplay())
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

		return ScheduledEventInPlayDTO
				.builder()
				.tournamentDto(tournaments)
				.tntSize(tournamentResponses.size())
				.matchSize(matchSize)
				.build();

	}


}
