package org.data.eightBet.dto;

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
	String date = "todayAndAll";

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
		private Integer tntSize;
		private Integer matchSize;
		private List<EventDTO.TournamentDTO> tournamentDto;
	}
}
