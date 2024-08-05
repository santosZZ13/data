package org.data.sofa.dto;

import lombok.*;

import java.util.List;

public interface SofaEventsByDateDTO {

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
		private List<SofaEventsDTO.EventDTO> eventDTOS;
	}



}
