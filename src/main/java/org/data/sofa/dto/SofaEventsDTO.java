package org.data.sofa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public interface SofaEventsDTO {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EventDTO {
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
		private Integer idTeam;
		private String name;
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
