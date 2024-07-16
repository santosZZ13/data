package org.data.eightBet.repository;

import lombok.AllArgsConstructor;
import org.data.eightBet.dto.EventInPlayDTO;
import org.data.persistent.entity.ScheduledEventsEightXBetEntity;
import org.data.persistent.repository.ScheduledEventsEightXBetMongoRepository;
import org.data.util.TournamentEightXBetConverter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.data.eightBet.dto.ScheduledEventInPlayEightXBetResponse.*;

@Repository
@AllArgsConstructor
public class EightXBetRepositoryImpl implements EightXBetRepository {

	private final ScheduledEventsEightXBetMongoRepository scheduledEventsEightXBetMongoRepository;

	@Override
	public List<EventInPlayDTO> getAllInPlayMatches() {
		return List.of();
	}

	@Override
	public List<ScheduledEventsEightXBetEntity> saveTournamentResponse(List<TournamentResponse> tournamentResponses) {

		List<ScheduledEventsEightXBetEntity> scheduledEventsEightXBetEntities = new ArrayList<>();

		for (int i = 0; i < tournamentResponses.size(); i++) {
			ScheduledEventsEightXBetEntity scheduledEventsEightXBetEntity = TournamentEightXBetConverter.convertToScheduledEventsEightXBetEntity(tournamentResponses.get(i));
			scheduledEventsEightXBetEntities.add(scheduledEventsEightXBetEntity);
		}

		scheduledEventsEightXBetMongoRepository.saveAll(scheduledEventsEightXBetEntities);
		return scheduledEventsEightXBetEntities;
	}
}
