package org.data.eightBet.repository;

import org.data.eightBet.dto.GetExBetEventByDate;
import org.data.eightBet.dto.ImportExBetFromFile;
import org.data.eightBet.response.ExBetMatchResponse;
import org.data.eightBet.response.ExBetTournamentResponse;
import org.data.persistent.entity.ExBetEntity;

import java.util.List;

public interface ExBetRepository {
	void updateInplayEvent();

//	List<ExBetEntity> saveTournamentResponse(List<ExBetTournamentResponse> tournamentResponses);

	List<ExBetEntity> getAllEventsEntity();

	void saveMatch(ExBetTournamentResponse tournament, ExBetMatchResponse match);

//	void saveMatchesMap(Map<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> tournamentMatchResponseMap);

	ImportExBetFromFile.ExBetResponseDto saveExBetEntity(List<ExBetTournamentResponse> exBetTournamentResponses);
	List<GetExBetEventByDate.ExBetMatchResponseDto> getExBetByDate(GetExBetEventByDate.Request request);
}
