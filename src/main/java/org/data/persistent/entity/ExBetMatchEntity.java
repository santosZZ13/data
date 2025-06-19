package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.entity.base.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
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
//	private Boolean isMatched;
//	private SofaDataEntity sofaDataEntity;

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
				Objects.equals(status, that.status);
//				Objects.equals(isMatched, that.isMatched);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, matchId, tournamentName, kickoffTime,
				homeId, homeName, awayId, awayName, round, isFavorite,
				status);
	}

	//	@Builder
//	@Data
//	@AllArgsConstructor
//	@NoArgsConstructor
//	public static class SofaDataEntity {
//		private Integer sofaMatchId;
//		private Integer sofaHomeId;
//		private Integer sofaAwayId;
//		private String sofaHomeName;
//		private String sofaAwayName;
//	}
//
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundEntity {
		private String roundName;
		private String roundType;
	}
}
