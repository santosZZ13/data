package org.data.eightBet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


public interface EventInPlayDTO {

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
