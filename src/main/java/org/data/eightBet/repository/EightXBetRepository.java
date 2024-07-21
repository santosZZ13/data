package org.data.eightBet.repository;

import org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse;
import org.data.persistent.entity.EventsEightXBetEntity;

import java.util.List;
import java.util.Map;

import static org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.*;

public interface EightXBetRepository {
	void updateInplayEvent();

	List<EventsEightXBetEntity> saveTournamentResponse(List<TournamentResponse> tournamentResponses);

	List<EventsEightXBetEntity> getAllEventsEntity();

	void saveMatch(TournamentResponse tournament, MatchResponse match);

	void saveMatchesMap(Map<TournamentResponse, MatchResponse> tournamentMatchResponseMap);
}
