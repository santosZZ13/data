package org.data.sofa.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;

import java.util.List;

public interface GetSofaEventsByDateDto {

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

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private GetSofaEventsByDateData data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class GetSofaEventsByDateData {
		private List<SfEventsDto.EventDto> events;
	}

}
