package org.data.sofa.repository.impl;

import lombok.AllArgsConstructor;
import org.data.conts.EventStatus;
import org.data.dto.sf.GetSofaEventHistoryDto;
import org.data.dto.sf.SfEventsCommonDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class SofaEventsTemplateRepositoryImpl implements SofaEventsTemplateRepository{

	private MongoTemplate mongoTemplate;

	@Override
	public List<GetSofaEventHistoryDto.HistoryScore> getHistoryScore(Integer teamId, EventStatus status, LocalDateTime from, LocalDateTime to) {

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

		if (from != null) {
			matchOperation = Aggregation.match(new Criteria().andOperator(
					Criteria.where("startTimestamp").gte(from)
			));
		}

		if (to != null) {
			matchOperation = Aggregation.match(new Criteria().andOperator(
					Criteria.where("startTimestamp").lte(to)
			));
		}

		if (status != null) {
			matchOperation = Aggregation.match(new Criteria().andOperator(
					Criteria.where("status.type").is(status.getStatus())
			));
		}

		Aggregation aggregation = Aggregation.newAggregation(
				matchOperation,
				projectionOperation,
				Aggregation.sort(Sort.Direction.DESC, "time")
		);


		AggregationResults<GetSofaEventHistoryDto.HistoryScore> results = mongoTemplate.aggregate(aggregation, "scheduled_events_sofascore", GetSofaEventHistoryDto.HistoryScore.class);
		return results.getMappedResults();
	}

	@Override
	public SfEventsCommonDto.TeamDetails getTeamDetailsById(Integer id) {
		MatchOperation matchOperation = Aggregation.match(Criteria.where("homeTeam._id").is(id));
		ProjectionOperation projectionOperation = Aggregation.project()
				.and("homeTeam.name").as("name")
				.and("homeTeam._id").as("idTeam")
				.and("homeTeam.country.name").as("country");

		Aggregation aggregation = Aggregation.newAggregation(
				matchOperation,
				projectionOperation
		);
		AggregationResults<SfEventsCommonDto.TeamDetails> scheduledEventsSofaScore = mongoTemplate.aggregate(aggregation, "scheduled_events_sofascore", SfEventsCommonDto.TeamDetails.class);
		if (!scheduledEventsSofaScore.getMappedResults().isEmpty()) {
			return scheduledEventsSofaScore.getMappedResults().get(0);
		}
		return null;
	}
}
