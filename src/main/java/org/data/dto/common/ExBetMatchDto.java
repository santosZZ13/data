package org.data.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExBetMatchDto {
	private int id;
	private String tournamentName;
	private String kickoffTime; // Changed to String for simplicity
	private int homeId;
	private String homeName;
	private int awayId;
	private boolean isFavorite;
	private String awayName;
	private RoundDto round;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundDto {
		private String roundName;
		private String roundType;
	}
}
