package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.entity.base.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.Objects;

@Document(collection = "exbet_matches")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExBetMatchEntity extends BaseEntity {
	private String id;
	private int matchId;
	private String tournamentName;
	private ZonedDateTime kickoffTime;
	private int homeId;
	private String homeName;
	private int awayId;
	private String awayName;
	private RoundEntity round;
	private boolean isFavorite;
	private String status; // notstarted, inprogress, finished
	private Boolean isMatched;
	private SofaDataEntity sofaDataEntity;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExBetMatchEntity that = (ExBetMatchEntity) o;
		return Objects.equals(matchId, that.matchId) &&
//				Objects.equals(kickoffTime, that.kickoffTime) &&
				Objects.equals(homeName, that.homeName) &&
				Objects.equals(awayName, that.awayName) &&
				Objects.equals(status, that.status) &&
				Objects.equals(isMatched, that.isMatched) &&
				Objects.equals(sofaDataEntity, that.sofaDataEntity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, matchId, tournamentName, kickoffTime,
				homeId, homeName, awayId, awayName, round, isFavorite,
				status);
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SofaDataEntity {
		private Integer sofaMatchId;
		private Integer sofaHomeId;
		private Integer sofaAwayId;
		private String sofaHomeName;
		private String sofaAwayName;
		private ScoreEntity homeScore;
		private ScoreEntity awayScore;

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			SofaDataEntity that = (SofaDataEntity) o;
			return
					Objects.equals(sofaHomeId, that.sofaHomeId) &&
					Objects.equals(sofaAwayId, that.sofaAwayId) &&
					Objects.equals(sofaHomeName, that.sofaHomeName) &&
					Objects.equals(sofaAwayName, that.sofaAwayName) &&
					Objects.equals(homeScore, that.homeScore) &&
					Objects.equals(awayScore, that.awayScore);
		}

		@Override
		public int hashCode() {
			return Objects.hash(sofaMatchId, sofaHomeId, sofaAwayId, sofaHomeName, sofaAwayName, homeScore, awayScore);
		}
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ScoreEntity {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normalTime;
		private Integer extra1;
		private Integer extra2;
		private Integer overtime;
		private Integer penalties;
		private Boolean scoreEmpty;
		private Integer aggregated;

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ScoreEntity that = (ScoreEntity) o;
			return Objects.equals(current, that.current) &&
					Objects.equals(display, that.display) &&
					Objects.equals(period1, that.period1) &&
					Objects.equals(period2, that.period2) &&
					Objects.equals(normalTime, that.normalTime) &&
					Objects.equals(extra1, that.extra1) &&
					Objects.equals(extra2, that.extra2) &&
					Objects.equals(overtime, that.overtime) &&
					Objects.equals(penalties, that.penalties) &&
					Objects.equals(scoreEmpty, that.scoreEmpty) &&
					Objects.equals(aggregated, that.aggregated);
		}
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundEntity {
		private String roundName;
		private String roundType;
	}
}
