package org.data.util.service;

import lombok.AllArgsConstructor;
import org.data.config.ApiConfig;
import org.data.exception.ExternalServiceException;
import org.data.response.sf.parent.SofaMatchResponse;
import org.data.response.sf.parent.SofaMatchResponseDetailDto;
import org.data.util.request.RestClient;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SofaApiService {
	private final RestClient<SofaMatchResponse> restClient;
	private final ApiConfig apiConfig;

	private static final String SCHEDULED_EVENTS_PATTERN = "/sport/football/scheduled-events/%s";
	private static final String SCHEDULED_EVENTS_INVERSE_PATTERN = "/sport/football/scheduled-events/%s/inverse";

	// https://www.sofascore.com/api/v1/team/50/events/last/0
	private static final String SCHEDULED_EVENTS_TEAM_PATTERN = "/team/%s/events/last/%s";
	private static final String SCHEDULED_EVENTS_TEAM_INVERSE_PATTERN = "/team/%s/events/last/%s";


	@Retryable(
			value = {org.springframework.web.client.RestClientException.class},
			maxAttempts = 4,
			backoff = @Backoff(delay = 1000, multiplier = 1.5)
	)
	public List<SofaMatchResponseDetailDto> getSofaMatchByDate(String date) {
		try {

			String scheduledEventUrl = apiConfig.getSofaBaseUrl() + String.format(SCHEDULED_EVENTS_PATTERN, date);
			String scheduledEventInverseUrl = apiConfig.getSofaBaseUrl() + String.format(SCHEDULED_EVENTS_INVERSE_PATTERN, date);

			// Gọi API cho endpoint 1
			SofaMatchResponse sofaMatchResponse = restClient.execute(
					scheduledEventUrl,
					HttpMethod.GET,
					null,
					null,
					SofaMatchResponse.class
			);

			// Gọi API cho endpoint 2
			SofaMatchResponse sofaMatchInverseResponse = restClient.execute(
					scheduledEventInverseUrl,
					HttpMethod.GET,
					null,
					null,
					SofaMatchResponse.class
			);

			List<SofaMatchResponseDetailDto> sofaMatchResponseDetailDtos = new ArrayList<>();
			if (!Objects.isNull(sofaMatchResponse) && Objects.nonNull(sofaMatchResponse.getEvents())) {
				sofaMatchResponseDetailDtos.addAll(sofaMatchResponse.getEvents());
			}

			if (Objects.nonNull(sofaMatchInverseResponse) && Objects.nonNull(sofaMatchInverseResponse.getEvents())) {
				sofaMatchResponseDetailDtos.addAll(sofaMatchInverseResponse.getEvents());
			}

			return sofaMatchResponseDetailDtos.stream()
					.filter(event -> {
						if (event.getStartTimestamp() == null) {
							return false;
						}
						ZonedDateTime eventDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(event.getStartTimestamp()), ZoneId.systemDefault());
						return date.equals(eventDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch matches from Sofa API for date: " + date, e);
		}
	}


	@Retryable(
			value = {org.springframework.web.client.RestClientException.class},
			maxAttempts = 4,
			backoff = @Backoff(delay = 1000, multiplier = 1.5)
	)
	public List<SofaMatchResponseDetailDto> getSofaTeamId(Integer teamId, Integer limit) {
		try {
			String scheduledEventTeamUrl = apiConfig.getSofaBaseUrl() + String.format(SCHEDULED_EVENTS_TEAM_PATTERN, teamId, 0);
			String scheduledEventTeamInverseUrl = apiConfig.getSofaBaseUrl() + String.format(SCHEDULED_EVENTS_TEAM_INVERSE_PATTERN, teamId, 0);
			// Gọi API cho endpoint 1
			SofaMatchResponse sofaMatchResponse = restClient.execute(
					scheduledEventTeamUrl,
					HttpMethod.GET,
					null,
					null,
					SofaMatchResponse.class
			);
			return sofaMatchResponse.getEvents().stream()
					.filter(match -> match.getStatus() != null && Objects.equals(match.getStatus().getType(), "finished"))
					.filter(match -> match.getStartTimestamp() != null)
					.sorted(Comparator.comparingLong(SofaMatchResponseDetailDto::getStartTimestamp).reversed())
					.limit(limit == null ? Integer.MAX_VALUE : limit)
					.toList();
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch matches from Sofa API for team Id: " + teamId, e);
		}
	}


	@Recover
	public List<SofaMatchResponseDetailDto> recoverGetSofaMatchByDate(org.springframework.web.client.RestClientException e, String date) {
		throw new ExternalServiceException(
				ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR,
				"Failed to fetch matches from Sofa API for date: " + date,
				e
		);
	}

	@Recover
	public List<SofaMatchResponseDetailDto> recoverGetSofaTeamId(org.springframework.web.client.RestClientException e, Integer teamId, Integer limit) {
		throw new ExternalServiceException(
				ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR,
				String.format("Failed to fetch history for team ID: %d", teamId),
				e
		);
	}
}