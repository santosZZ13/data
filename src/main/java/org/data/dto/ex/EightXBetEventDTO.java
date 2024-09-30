package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public interface EightXBetEventDTO {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetTournamentDTO {
		private String tntName;
		private Integer count;
		private List<EightXBetMatchDTO> matches;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EightXBetMatchDTO {
		private Integer iid;
		private Boolean inPlay;
		private String homeName;
		private String awayName;
		private String slug;
		private LocalDateTime kickoffTime;
		@JsonProperty("sofa_detail")
		private SofaEvent sofaDetail;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SofaEvent {
		@JsonProperty("team_1")
		private Team firstTeam;
		@JsonProperty("team_2")
		private Team secondTeam;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Team {
		private Integer id;
		private String name;
	}
}
