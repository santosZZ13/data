package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledEventInPlayDTO {

	private List<TournamentDTO> tournamentResponses;


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentDTO {
		private String tntName;
		private List<MatchDTO> matches;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MatchDTO {
		private Boolean inPlay;
		private String homeName;
		private String awayName;
		private String slug;
		private LocalDateTime kickoffTime;
	}
}
