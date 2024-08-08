package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public interface GetSofaEventHistoryDTO {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		@JsonProperty("total_matches")
		private int totalMatches;
		private List<HistoryScore> historyScores;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class HistoryScore {
		private String tournamentName;
		private Integer againstId;
		private String against;
		private LocalDateTime time;
		@JsonProperty("home_score")
		private Score homeScore;
		@JsonProperty("away_score")
		private Score againstScore;
		private String status;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Score {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normaltime;
		private Integer extra1;
		private Integer extra2;
		private Integer overtime;
		private Integer penalties;

		public Boolean isScoreEmpty() {
			return current == null && display == null && period1 == null && period2 == null && normaltime == null
					&& extra1 == null && extra2 == null && overtime == null && penalties == null;
		}
	}
}
