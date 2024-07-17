package org.data.eightBet.repository;

import org.data.eightBet.dto.EventInPlayDTO;
import org.data.persistent.entity.EventsEightXBetEntity;

import java.util.List;

import static org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.*;

public interface EightXBetRepository {
	List<EventInPlayDTO> getAllInPlayMatches();
	List<EventsEightXBetEntity> saveTournamentResponse(List<TournamentResponse> tournamentResponses);
}
