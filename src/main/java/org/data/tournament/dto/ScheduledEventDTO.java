package org.data.tournament.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

public interface ScheduledEventDTO {

	@EqualsAndHashCode(callSuper = true)
	@Data
	@SuperBuilder
	@NoArgsConstructor
	@AllArgsConstructor
	class Request extends PaginationDTO {
		private String date;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	class Response {
		private ScheduledEventsCommonResponse.TournamentResponse tournament;
		private ScheduledEventsCommonResponse.SeasonResponse session;
		private ScheduledEventsCommonResponse.RoundInfo round;
	}




//	@Data
//	@Builder
//	@NoArgsConstructor
//	@AllArgsConstructor
//	class TournamentDTO {
//		private String name;
//		private String category;
//	}
//
//	@Data
//	@Builder
//	@NoArgsConstructor
//	@AllArgsConstructor
//	class SessionDTO {
//		private String name;
//		private String year;
//	}
//
//	@Data
//	@Builder
//	@NoArgsConstructor
//	@AllArgsConstructor
//	class RoundInfoDTO {
//		private String name;
//		private String year;
//	}
}
