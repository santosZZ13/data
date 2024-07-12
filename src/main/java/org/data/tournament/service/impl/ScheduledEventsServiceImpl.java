package org.data.tournament.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.sap.service.SapService;
import org.data.tournament.dto.GenericResponseWrapper;
import org.data.tournament.dto.ScheduledEventDTO;
import org.data.tournament.dto.ScheduledEventsResponse;
import org.data.tournament.repository.impl.ScheduledEventsRepository;
import org.data.tournament.service.ScheduledEventsService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class ScheduledEventsServiceImpl implements ScheduledEventsService {

	private final ScheduledEventsRepository scheduledEventsRepository;
	private final SapService sapService;

	@Override
	public GenericResponseWrapper getAllScheduleEventsByDate(ScheduledEventDTO.Request request) {
		String url = "https://www.sofascore.com/api/v1/sport/football/scheduled-events/2024-07-09";
		RestTemplate restTemplate = new RestTemplate();
//		HttpEntity<String> entity = new HttpEntity<>();
		ResponseEntity<ScheduledEventsResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, ScheduledEventsResponse.class);
		ScheduledEventsResponse scheduledEventsResponse = response.getBody();


		// Call Sofa
		// Call DB
		// Compare data -> update final data -> return data

//			sapService.requestGet(url, ScheduledEventsResponse.class);

//		List<ScheduledEventsEntity> scheduledEventsEntities = scheduledEventsRepository.saveEvents(eventsResponse);

		List<ScheduledEventsResponse.Event> eventsResponse = scheduledEventsResponse.getEvents();
		List<ScheduledEventsResponse.Event> events = scheduledEventsRepository.getAllEvents();






		if (eventsResponse == null) {
			throw new RuntimeException("Can't fetch events from remote");
		}







		List<Integer> eventsResponseId = eventsResponse
				.stream()
				.map(ScheduledEventsResponse.Event::getId)
				.toList();
		List<Integer> eventsId = events
				.stream()
				.map(ScheduledEventsResponse.Event::getId)
				.toList()
				.stream().sorted()
				.toList();


		List<ScheduledEventsResponse.Event> newEvents = new ArrayList<>();

		eventsResponse.forEach(eventRp -> {
			Integer eventRpId = eventRp.getId();
			if (!eventsId.contains(eventRpId)) {
				newEvents.add(eventRp);
			} else {
				ScheduledEventsResponse.Event event = events.stream()
						.filter(e -> Objects.equals(e.getId(), eventRpId))
						.findFirst()
						.orElse(null);

				if (!Objects.isNull(event)) {
					if (!event.equals(eventRp)) {
						eventRp.setIdDB(event.getIdDB());
						scheduledEventsRepository.saveEvent(eventRp);
					}
				}

			}
		});


		if (!newEvents.isEmpty()) {
			log.info("New events found: {}", newEvents.size());
			scheduledEventsRepository.saveEvents(newEvents);
		}



		return null;
	}
}
