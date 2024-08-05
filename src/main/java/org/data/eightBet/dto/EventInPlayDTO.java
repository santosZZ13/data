package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


public interface EventInPlayDTO {

	String EIGHT_X_BET = "/product/business/sport/tournament/info";
	int S_ID = 1;
	String SORT = "tournament";
	boolean IN_PLAY = true;
	String LANGUAGE = "en-us";


	 static Map<String, Object> queryParams() {
		return Map.of("sid", S_ID, "sort", SORT,
				"inplay", IN_PLAY, "language", LANGUAGE);
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private Integer tntSize;
		private Integer matchSize;
		private List<EightXBetEventDTO.EightXBetTournamentDTO> eightXBetTournamentDto;
	}

}
