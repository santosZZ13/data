package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public interface EventDTO {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TournamentDTO {
		private String tntName;
		private Integer count;
		private List<MatchDTO> matches;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class MatchDTO {
		private Integer iid;
		private Boolean inPlay;
		private String homeName;
		private String awayName;
		private String slug;
		private LocalDateTime kickoffTime;
		private SofaEvent sofaEvent;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SofaEvent {
		private Team home;
		private Team away;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Team {
		private Integer idTeam;
		private String name;
	}
}
