package org.data.eightBet.repository;

import org.data.eightBet.dto.EightXBetCommonResponse;
import org.data.persistent.entity.EventsEightXBetEntity;

import java.util.List;
import java.util.Map;

import static org.data.eightBet.dto.EightXBetEventsResponse.*;

public interface EightXBetRepository {
	void updateInplayEvent();

	List<EventsEightXBetEntity> saveTournamentResponse(List<EightXBetTournamentResponse> tournamentResponses);

	List<EventsEightXBetEntity> getAllEventsEntity();

	void saveMatch(EightXBetTournamentResponse tournament, EightXBetCommonResponse.EightXBetMatchResponse match);

	void saveMatchesMap(Map<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> tournamentMatchResponseMap);
}
