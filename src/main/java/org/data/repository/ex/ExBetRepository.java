package org.data.repository.ex;

import org.data.dto.common.MatchedMatchesDto;
import org.data.dto.common.ExBetMatchResponseDto;

import java.util.List;

public interface ExBetRepository {
	void saveExBetMatchDto(List<ExBetMatchResponseDto> matchesDto);

	List<ExBetMatchResponseDto> getExBetByDate(String date);


	void updateStatusByIds(List<Integer> matchIds, String status);
}
