package org.data.repository.ex;

import org.data.dto.ex.ExCommonDto;
import org.data.dto.ex.ImportExBetFromFile;
import org.data.response.ex.ExBetMatchResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.persistent.entity.ExBetEntity;

import java.util.List;

public interface ExBetRepository {
	void updateInplayEvent();

//	List<ExBetEntity> saveTournamentResponse(List<ExBetTournamentResponse> tournamentResponses);

	List<ExBetEntity> getAllEventsEntity();

	void saveMatch(ExBetTournamentResponse tournament, ExBetMatchResponse match);

//	void saveMatchesMap(Map<EightXBetTournamentResponse, EightXBetCommonResponse.EightXBetMatchResponse> tournamentMatchResponseMap);

	ImportExBetFromFile.ExBetResponseDto saveExBetEntity(List<ExBetTournamentResponse> exBetTournamentResponses);
	List<ExCommonDto.ExMatchResponseDto> getExBetByDate(String date);
}
