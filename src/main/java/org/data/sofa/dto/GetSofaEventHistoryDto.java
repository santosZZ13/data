package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;
import org.data.conts.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface GetSofaEventHistoryDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private int teamId;
		private EventStatus status;
		private LocalDateTime from;
		private LocalDateTime to;
	}


	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private GetSofaEventHistoryData data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class GetSofaEventHistoryData {
		@JsonProperty("total_matches")
		private int totalMatches;
		@JsonProperty("team_details")
		private SofaEventsDto.TeamDetails teamDetails;
		@JsonProperty("history_scores")
		private List<HistoryScore> historyScores;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class HistoryScore {
		private String tournamentName;
		@JsonProperty("against_id")
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
