package org.data.repository.ex;

import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.ex.GetMatchesExByDateDto;
import org.data.dto.common.ExBetMatchDto;

import java.util.List;

public interface ExBetRepository {
	int saveExBetMatchDto(List<ExBetMatchDto> matchesDto);

	List<GetMatchesExByDateDto.ExBetMatchDto> getExBetByDate(String[] date, boolean isFavorite);

	MatchedMatchesDto getMatchedMatch(ExBetMatchDto matchDto);
}
