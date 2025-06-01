package org.data.persistent.repository;

import org.data.persistent.entity.TeamEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMongoRepository extends MongoRepository<TeamEntity, String> {
	Optional<TeamEntity> findBySofaTeamInfoTeamId(Integer teamId);

	Optional<TeamEntity> findByExBetTeamInfoTeamId(Integer teamId);

	Optional<TeamEntity> findBySofaTeamInfoTeamIdOrExBetTeamInfoTeamId(Integer sofaTeamId, Integer exBetTeamId);

	List<TeamEntity> findBySofaTeamInfoTeamIdIn(List<Integer> teamIds);

	List<TeamEntity> findByExBetTeamInfoTeamIdIn(List<Integer> teamIds);

}
