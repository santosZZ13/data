package org.data.tournament.service.impl;

import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.tournament.service.ScheduledEventsService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ScheduledEventsServiceImpl implements ScheduledEventsService {

	@Override
	public List<ScheduledEventsResponse> getAllScheduleEventsByDate() {
		String url = "https://www.sofascore.com/api/v1/sport/football/scheduled-events/2024-07-09";
		RestTemplate restTemplate = new RestTemplate();
//		HttpEntity<String> entity = new HttpEntity<>();


		ResponseEntity<ScheduledEventsResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, ScheduledEventsResponse.class);
		ScheduledEventsResponse scheduledEventsResponse = response.getBody();


		List<ScheduledEventsResponse.Event> events = scheduledEventsResponse.getEvents();


		return null;
	}
}
