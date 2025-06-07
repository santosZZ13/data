package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.entity.base.BaseEntity;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

@Document(collection = "sofa_scheduled_matches")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@CompoundIndexes({
		@CompoundIndex(name = "matchId_idx", def = "{'matchId': 1}", unique = true)
})
public class SofaScheduledMatchEntity extends BaseEntity {
	private String id;
	private Integer matchId;
	private ZonedDateTime startTimestamp; // Đổi từ LocalDateTime sang ZonedDateTime
	private TournamentEntity tournamentInfo;
	private SessionEntity sessionInfo;
	private RoundEntity roundInfo;
	private StatusEntity status;
	private TeamEntity homeTeam;
	private TeamEntity awayTeam;
	private ScoreEntity homeScore;
	private ScoreEntity awayScore;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SofaScheduledMatchEntity sofaScheduledMatchEntity = (SofaScheduledMatchEntity) o;
		return Objects.equals(status, sofaScheduledMatchEntity.status) &&
				Objects.equals(homeScore, sofaScheduledMatchEntity.homeScore) &&
				Objects.equals(awayScore, sofaScheduledMatchEntity.awayScore) &&
				Objects.equals(homeTeam, sofaScheduledMatchEntity.homeTeam) &&
				Objects.equals(awayTeam, sofaScheduledMatchEntity.awayTeam);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, matchId, startTimestamp, tournamentInfo, sessionInfo, roundInfo,
				status, homeTeam, awayTeam, homeScore, awayScore);
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentEntity {
		private Integer id;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SessionEntity {
		private Integer id;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundEntity {
		private Integer round;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class StatusEntity {
		private String description;
		private String type; // finished, notstarted, postponed, canceled, inprogress

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			StatusEntity that = (StatusEntity) o;
			return Objects.equals(description, that.description) && Objects.equals(type, that.type);
		}

		@Override
		public int hashCode() {
			return Objects.hash(description, type);
		}
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TeamEntity {
		private Integer id;
		private String name;
		private String normalizedName;
		private String shortName;

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TeamEntity that = (TeamEntity) o;
			return Objects.equals(id, that.id) && Objects.equals(name, that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, name);
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
					Objects.equals(scoreEmpty, that.scoreEmpty);
		}

		@Override
		public int hashCode() {
			return Objects.hash(current, display, period1, period2, normalTime, extra1, extra2, overtime, penalties, scoreEmpty);
		}
	}
}
