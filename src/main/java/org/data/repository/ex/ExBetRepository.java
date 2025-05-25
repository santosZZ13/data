package org.data.repository.ex;

import org.data.dto.GetMatchesExByDateDto;
import org.data.dto.common.ExBetMatchDto;

import java.util.List;

public interface ExBetRepository {
	int saveExBetMatchDto(List<ExBetMatchDto> matchesDto);

	List<GetMatchesExByDateDto.ExBetMatchDto> getExBetByDate(String[] date);
}
