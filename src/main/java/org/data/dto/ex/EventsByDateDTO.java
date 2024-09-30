package org.data.dto.ex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public interface EventsByDateDTO {
	String GET_EVENTS_BY_DATE = "/product/business/sport/prematch/tournament";
	Integer S_ID = 1;
	String SORT = "tournament";
	Boolean IN_PLAY = false;
	String date = "20240803";

	static Map<String, Object> queryParams() {
		return Map.of("sid", S_ID,
				"sort", SORT,
				"inplay", IN_PLAY,
				"date", date);
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		@JsonProperty("total_matches")
		private Integer totalMatches;
		@JsonProperty("total_tournaments")
		private Integer totalTournaments;
		@JsonProperty("tournament_found")
		private TournamentDTO tournamentsWithId;
		@JsonProperty("tournament_no_found")
		private TournamentDTO tournamentsWithNoId;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TournamentDTO {
		@JsonProperty("total_tournaments")
		private Integer tntSize;
		@JsonProperty("total_matches")
		private Integer matchSize;
		@JsonProperty("tournaments")
		private List<EightXBetEventDTO.EightXBetTournamentDTO> eightXBetTournamentDto;
	}
}
