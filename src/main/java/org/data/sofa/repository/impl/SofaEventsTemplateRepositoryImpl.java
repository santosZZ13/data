package org.data.sofa.repository.impl;

import lombok.AllArgsConstructor;
import org.data.sofa.dto.GetSofaEventHistoryDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class SofaEventsTemplateRepositoryImpl implements SofaEventsTemplateRepository{

	private MongoTemplate mongoTemplate;

	@Override
	public List<GetSofaEventHistoryDTO.HistoryScore> getHistoryScore(Integer teamId) {

		MatchOperation matchOperation = Aggregation.match(new Criteria().orOperator(
				Criteria.where("homeTeam._id").is(teamId),
				Criteria.where("awayTeam._id").is(teamId)
		));

		ProjectionOperation projectionOperation = Aggregation.project("tournament.name")
				.and(ConditionalOperators
						.when(Criteria.where("homeTeam._id").is(teamId))
						.thenValueOf("awayTeam.name")
						.otherwiseValueOf("homeTeam.name"))
				.as("against")
				.and(ConditionalOperators.when(Criteria.where("homeTeam._id").is(teamId))
						.thenValueOf("awayTeam._id")
						.otherwiseValueOf("homeTeam._id"))
				.as("againstId")
				.and(ConditionalOperators.when(Criteria.where("homeTeam._id").is(teamId))
						.thenValueOf("homeScore")
						.otherwiseValueOf("awayScore"))
				.as("homeScore")
				.and(ConditionalOperators.when(Criteria.where("homeTeam._id").is(teamId))
						.thenValueOf("awayScore")
						.otherwiseValueOf("homeScore"))
				.as("againstScore")
				.andExpression("status.type").as("status")
				.andExpression("startTimestamp").plus(7 * 3600000).as("time");


		Aggregation aggregation = Aggregation.newAggregation(
				matchOperation,
				projectionOperation,
				Aggregation.sort(Sort.Direction.DESC, "time")
		);


		AggregationResults<GetSofaEventHistoryDTO.HistoryScore> results = mongoTemplate.aggregate(aggregation, "scheduled_events_sofascore", GetSofaEventHistoryDTO.HistoryScore.class);
		return results.getMappedResults();
	}
}
