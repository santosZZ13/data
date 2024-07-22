package org.data.sofa.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.service.sap.SapService;
import org.data.common.model.GenericResponseWrapper;
import org.data.sofa.dto.ScheduledEventDTO;
import org.data.sofa.dto.ScheduledEventsResponse;
import org.data.sofa.repository.impl.ScheduledEventsRepository;
import org.data.sofa.service.ScheduledEventsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
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



//			sapService.requestGet(url, ScheduledEventsResponse.class);

//		List<ScheduledEventsEntity> scheduledEventsEntities = scheduledEventsRepository.saveEvents(eventsResponse);

		List<ScheduledEventsResponse.Event> eventsResponse = scheduledEventsResponse.getEvents();
		List<ScheduledEventsResponse.Event> events = scheduledEventsRepository.getAllEvents();


		sapService.restSofaScoreGet("213", ScheduledEventsResponse.Event.class);


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


		Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize());
		ScheduledEventsResponse.Event eventsByDate = scheduledEventsRepository.getAllEventByDate(getLocalDateTime(request.getDate()), pageable);


		return GenericResponseWrapper
				.builder()
				.code("")
				.msg("")
				.data(eventsByDate)
				.build();
	}

	private LocalDateTime getLocalDateTime(String date) {
		return LocalDateTime.parse(date);
	}
}
