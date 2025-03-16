package org.data.repository;

import lombok.AllArgsConstructor;
import org.data.dto.MatchDto;
import org.data.persistent.repository.MatchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@AllArgsConstructor
public class SofaRepositoryImpl implements SofaRepository{

	private final MatchRepository matchRepository;

	@Override
	public List<MatchDto> getMatchesDto(int homeTeamId, int awayTeamId) {

		matchRepository.findByTeamsHomeTeamIdOrTeamsAwayTeamId(homeTeamId, awayTeamId);

		return List.of();
	}

	@Override
	public List<MatchDto> getMatchesWithPendingStatus(Integer teamId, Long lastMatchTimestamp, Long futureMatchTimestamp) {
		return List.of();
	}

	@Override
	public MatchDto findMatchByMatchId(int matchId) {
		return null;
	}

	@Override
	public void saveMatches(List<MatchDto> matchesToSave) {

	}
}
