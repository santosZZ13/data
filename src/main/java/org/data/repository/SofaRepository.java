package org.data.repository;

import org.data.dto.MatchDto;

import java.util.List;

public interface SofaRepository {
	List<MatchDto> getMatchesDto(int homeTeamId, int awayTeamId);

	List<MatchDto> getMatchesWithPendingStatus(Integer teamId, Long lastMatchTimestamp, Long futureMatchTimestamp);

	MatchDto findMatchByMatchId(int matchId);

	void saveMatches(List<MatchDto> matchesToSave);
}
