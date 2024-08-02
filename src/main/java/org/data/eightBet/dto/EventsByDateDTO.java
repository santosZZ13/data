package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public interface EventsByDateDTO {
	String GET_EVENTS_BY_DATE = "/product/business/v2/sport/prematch/category";
	Integer S_ID = 1;
//	String SORT = "tournament";
//	Boolean IN_PLAY = false;
	String date = "20240801";

	static Map<String, Object> queryParams() {
		return Map.of("sid", S_ID, "date", date);
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
