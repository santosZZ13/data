package org.data.util.service;

import lombok.AllArgsConstructor;
import org.data.config.ApiConfig;
import org.data.response.sf.parent.SofaMatchResponse;
import org.data.response.sf.parent.SofaMatchResponseDetail;
import org.data.util.request.RestClient;
import org.data.util.utils.DateUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
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

	/**
	 * Lấy danh sách trận đấu từ Sofa API theo ngày
	 *
	 * @param date - Ngày cần lấy (định dạng "yyyy-MM-dd")
	 * @return Danh sách trận đấu
	 */
	public List<SofaMatchResponseDetail> getSofaMatchByDate(String date) {
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

			List<SofaMatchResponseDetail> sofaMatchResponseDetails = new ArrayList<>();
			if (!Objects.isNull(sofaMatchResponse) && Objects.nonNull(sofaMatchResponse.getEvents())) {
				sofaMatchResponseDetails.addAll(sofaMatchResponse.getEvents());
			}

			if (Objects.nonNull(sofaMatchInverseResponse) && Objects.nonNull(sofaMatchInverseResponse.getEvents())) {
				sofaMatchResponseDetails.addAll(sofaMatchInverseResponse.getEvents());
			}

			return sofaMatchResponseDetails.stream()
					.filter(event -> {
						if (event.getStartTimestamp() == null) {
							return false;
						}
						String eventDate = DateUtils.formatDate(Instant.ofEpochSecond(event.getStartTimestamp()));
						return date.equals(eventDate);
					})
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new RuntimeException("Failed to fetch matches from Sofa API for date: " + date, e);
		}
	}
}