package org.data.repository;

import org.data.dto.TeamDto;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepositoryImpl implements TeamRepository {


	@Override
	public TeamDto getTeamById(int teamId) {
		return null;
	}

	@Override
	public void saveTeam(TeamDto team) {

	}


}
