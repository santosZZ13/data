package org.data.repository.ex;

import org.data.dto.common.ExBetMatchDto;

import java.util.List;

public interface ExBetRepository {
	void saveExBetMatchDto(List<ExBetMatchDto> matchesDto);

	List<ExBetMatchDto> getExBetByDate(String date);

	void updateStatusByIds(List<Integer> matchIds, String status);
}
