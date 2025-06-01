package org.data.repository.team;

import org.data.dto.common.SofaMatchDto;
import org.data.dto.common.TeamDto;
import org.data.persistent.entity.TeamEntity;

import java.util.List;

public interface TeamRepository {
	void saveTeam(TeamDto teamDto);
	void saveTeamsFromSofa(List<TeamDto> teamDtos);

}
