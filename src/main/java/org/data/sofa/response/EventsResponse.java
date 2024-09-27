package org.data.sofa.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventsResponse {
	private List<EventChildResponse> events;
	private Boolean hasNextPage;
}
