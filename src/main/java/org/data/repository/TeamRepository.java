package org.data.repository;

import org.data.dto.TeamDto;

public interface TeamRepository {
	TeamDto getTeamById(int teamId);
	void saveTeam(TeamDto team);
}
