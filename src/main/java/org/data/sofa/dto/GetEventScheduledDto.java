package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface GetEventScheduledDto {

	String SCHEDULED_EVENTS = "/sport/football/scheduled-events/";
	String SCHEDULED_EVENTS_INVERSE = "/inverse";

	String SCHEDULED_EVENT_TEAM_LAST = "/team/{}/events/last/{}";
	String SCHEDULED_EVENT_TEAM_NEXT = "/team/{}/events/next/{}";

	String GET_SCHEDULED_EVENT_BY_DATE_API = "/scheduled-events";


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		String date;
	}

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private ScheduledEventByDate data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScheduledEventByDate {
		private List<ScheduledEventDto> events;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScheduledEventDto {
		private String tntName;
		private String seasonName;
		private Integer round;
		private String status;
		private TeamDetailsDto homeDetails;
		private TeamDetailsDto awayDetails;
		private ScoreDetailsDto homeScoreDetails;
		private ScoreDetailsDto awayScoreDetails;
		private Integer id;
		private LocalDateTime kickOffMatch;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamDetailsDto {
		@JsonProperty("id")
		private Integer idTeam;
		private String name;
		private String country;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScoreDetailsDto {
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
