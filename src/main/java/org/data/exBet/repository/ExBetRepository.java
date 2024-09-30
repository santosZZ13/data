package org.data.exBet.repository;

import org.data.dto.ex.ExBetCommonDto;
import org.data.dto.ex.ImportExBetFromFile;
import org.data.exBet.response.ExBetMatchResponse;
import org.data.exBet.response.ExBetTournamentResponse;
import org.data.persistent.entity.ExBetEntity;

import java.util.List;

public interface ExBetRepository {
	void updateInplayEvent();

//	List<ExBetEntity> saveTournamentResponse(List<ExBetTournamentResponse> tournamentResponses);

	List<ExBetEntity> getAllEventsEntity();

	void saveMatch(ExBetTournamentResponse tournament, ExBetMatchResponse match);

//	void saveMatchesMap(Map<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> tournamentMatchResponseMap);

	ImportExBetFromFile.ExBetResponseDto saveExBetEntity(List<ExBetTournamentResponse> exBetTournamentResponses);
	List<ExBetCommonDto.ExBetMatchResponseDto> getExBetByDate(String date);
}
