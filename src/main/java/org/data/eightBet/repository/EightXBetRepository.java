package org.data.eightBet.repository;

import org.data.eightBet.dto.ScheduledEventInPlayDTO;

import java.util.List;

public interface EightXBetRepository {
	List<ScheduledEventInPlayDTO> getAllInPlayMatches();
}
