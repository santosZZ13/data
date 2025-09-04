package org.data.repository;

import org.data.dto.common.ex.ExBetMatchDto;

import java.util.List;

public interface ExBetRepository {
	void saveExBetMatchDto(List<ExBetMatchDto> matchesDto);

	List<ExBetMatchDto> getExBetByDate(String date);

	void updateStatusByIds(List<Integer> matchIds, String status);
}
