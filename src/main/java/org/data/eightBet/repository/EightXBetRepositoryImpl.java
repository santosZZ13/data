package org.data.eightBet.repository;

import org.data.eightBet.dto.ScheduledEventInPlayDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EightXBetRepositoryImpl implements EightXBetRepository {

	@Override
	public List<ScheduledEventInPlayDTO> getAllInPlayMatches() {
		return List.of();
	}
}
