package org.data.sofa.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public interface SofaScheduledEventByDateDTO {

	String SCHEDULED_EVENTS = "/sport/football/scheduled-events/";
	String SCHEDULED_EVENTS_INVERSE = "/inverse";

	String SCHEDULED_EVENT_TEAM_LAST = "/team/{}/events/last/{}";
	String SCHEDULED_EVENT_TEAM_NEXT = "/team/{}/events/next/{}";


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		String date;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private List<EventDetail> eventDetails;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class EventDetail {
		private String tntName;
		private String seasonName;
		private Integer round;
		private String status;
		private String homeName;
		private String awayName;
		private ScoreDetails homeScoreDetails;
		private ScoreDetails awayScoreDetails;
		private Integer id;
		private LocalDateTime kickOffMatch;
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
