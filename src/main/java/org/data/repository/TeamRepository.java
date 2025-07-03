package org.data.repository;

import org.data.dto.common.TeamDto;

import java.util.List;

public interface TeamRepository {
	void saveTeam(TeamDto teamDto);
	void saveTeamsFromSofa(List<TeamDto> teamDtos);

}
