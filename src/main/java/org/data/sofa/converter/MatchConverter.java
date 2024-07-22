//package org.data.tournament.converter;
//
//import org.data.tournament.dto.MatchModel;
//import org.data.persistent.entity.ScheduledEventsEightXBetEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class MatchConverter {
//	public List<ScheduledEventsEightXBetEntity> convertToMatchEntity(List<MatchModel> matchModels) {
//		List<ScheduledEventsEightXBetEntity> matchEntities = new ArrayList<>();
//		matchModels.forEach(matchModel -> {
//			ScheduledEventsEightXBetEntity scheduledEventsEightXBetEntity = ScheduledEventsEightXBetEntity.builder()
//					.tnName(matchModel.getTnName())
//					.name(matchModel.getName())
//					.round(matchModel.getRound().getRoundName())
//					.home(matchModel.getHome())
//					.away(matchModel.getAway())
//					.kickoffTime(matchModel.getKickoffTime())
//					.build();
//			matchEntities.add(scheduledEventsEightXBetEntity);
//		});
//		return matchEntities;
//	}
//}
