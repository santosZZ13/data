package org.data.persistent.repository;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import org.data.persistent.entity.MatchEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface MatchRepository extends MongoRepository<MatchEntity, String> {
	List<MatchEntity> findByDate(Date date);
	MatchEntity findByMatchId(Long matchId);
	List<MatchEntity> findByTeamsHomeTeamIdOrTeamsAwayTeamId(int homeTeamId, int awayTeamId);
}
