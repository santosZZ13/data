package org.data.tournament.converter;

import org.data.tournament.dto.MatchModel;
import org.data.tournament.persistent.entity.MatchEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MatchConverter {
	public List<MatchEntity> convertToMatchEntity(List<MatchModel> matchModels) {
		List<MatchEntity> matchEntities = new ArrayList<>();
		matchModels.forEach(matchModel -> {
			MatchEntity matchEntity = MatchEntity.builder()
					.tnName(matchModel.getTnName())
					.name(matchModel.getName())
					.round(matchModel.getRound().getRoundName())
					.home(matchModel.getHome())
					.away(matchModel.getAway())
					.kickoffTime(matchModel.getKickoffTime())
					.build();
			matchEntities.add(matchEntity);
		});
		return matchEntities;
	}
}
