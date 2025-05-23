package org.data.persistent.entity;

import lombok.*;
import org.data.dto.ImportMatchesJsonFile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "exbet_matches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExBetMatchEntity {
	private String id;
	private String tournamentName;
	private LocalDateTime kickoffTime;
	private int homeId;
	private String homeName;
	private int awayId;
	private String awayName;
	private RoundEntity round;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundEntity {
		private String roundName;
		private String roundType;
	}
}
