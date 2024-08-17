package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public interface SofaEventsDto {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EventDto {
		private String tntName;
		private String seasonName;
		private Integer round;
		private String status;
		private TeamDetails homeDetails;
		private TeamDetails awayDetails;
		private ScoreDetails homeScoreDetails;
		private ScoreDetails awayScoreDetails;
		private Integer id;
		private LocalDateTime kickOffMatch;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamDetails {
		@JsonProperty("id")
		private Integer idTeam;
		private String name;
		private String country;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScoreDetails {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normalTime;
		private Integer extra1;
		private Integer extra2;
		private Integer overtime;
		private Integer penalties;
	}
}
