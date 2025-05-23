package org.data.repository.ex;

import org.data.dto.GetMatchesExByDate;
import org.data.dto.ImportMatchesJsonFile;
import org.data.dto.ex.ExCommonDto;
import org.data.dto.ex.ImportExBetFromFile;
import org.data.response.ex.ExBetMatchResponse;
import org.data.response.ex.ExBetTournamentResponse;
import org.data.persistent.entity.ExBetEntity;

import java.util.List;

public interface ExBetRepository {
	void saveExBetMatchDto(List<ImportMatchesJsonFile.ExBetMatchDto> exBetMatchDtos);

	List<GetMatchesExByDate.ExBetMatchDto> getExBetByDate(String date);
}
