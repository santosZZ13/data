package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.dto.ImportMatchesJsonFile;
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

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundEntity {
		private String roundName;
		private String roundType;
	}
}
