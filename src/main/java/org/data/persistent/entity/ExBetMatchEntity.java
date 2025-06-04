package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.entity.base.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "exbet_matches")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class ExBetMatchEntity extends BaseEntity {
	private String id;
	private int matchId;
	private String tournamentName;
	private LocalDateTime kickoffTime;
	private int homeId;
	private String homeName;
	private int awayId;
	private String awayName;
	private RoundEntity round;
	private boolean isFavorite;
	private Boolean isMatched;
	private SofaDataEntity sofaDataEntity;

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
