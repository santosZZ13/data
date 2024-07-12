package org.data.tournament.service.impl;

import lombok.AllArgsConstructor;
import org.data.tournament.converter.MatchConverter;
import org.data.tournament.dto.MatchModel;
import org.data.tournament.dto.Tournament8xResponse;
import org.data.tournament.persistent.repository.MatchRepository;
import org.data.tournament.service.PreMatchService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PreMatchServiceImpl implements PreMatchService {

	private final MatchRepository matchRepository;
	private final MatchConverter matchConverter;

	@Override
	public List<Tournament8xResponse> getAllTour() {
		RestTemplate restTemplate = new RestTemplate();

		// Set the URL
		String url = "https://vd001-fxh9-api.fdsgrtg.8xrgfgfgfdw.com/product/business/sport/prematch/tournament?sid=1&date=todayAndAll&sort=tournament&inplay=false";

		// Create HttpHeaders and set the headers
		HttpHeaders headers = new HttpHeaders();
		headers.set("accept", "application/json, text/plain, */*");
		headers.set("accept-language", "en-us");
		headers.set("apptype", "2");
		headers.set("browser", "Chrome 126.0.0.0");
		headers.set("currency", "nVND");
		headers.set("device", "mobile");
		headers.set("origin", "https://8x6895.com");
		headers.set("priority", "u=1, i");
		headers.set("referer", "https://8x6895.com/");
		headers.set("screen", "1028x945");
		headers.set("sec-ch-ua", "\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"");
		headers.set("sec-ch-ua-mobile", "?0");
		headers.set("sec-ch-ua-platform", "\"Windows\"");
		headers.set("sec-fetch-dest", "empty");
		headers.set("sec-fetch-mode", "cors");
		headers.set("sec-fetch-site", "cross-site");
		headers.set("time-zone", "GMT+07:00");
		headers.set("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
		headers.set("x-uuid", "4fcb9c0e69be8f554c48b43980cf0bfe");

		// Create the HttpEntity with the headers
		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Make the GET request
		ResponseEntity<Tournament8xResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, Tournament8xResponse.class);

		Tournament8xResponse tournament8xResponse = response.getBody();



		List<Tournament8xResponse.Tournament> tournaments = tournament8xResponse.getData().getTournaments();
		List<MatchModel> matchesOfTour = new ArrayList<>();

		tournaments.forEach(tournament -> {
			String tnName = tournament.getName();

			List<Tournament8xResponse.Match> matches = tournament.getMatches();
			matches.forEach(match -> {
				String home = match.getHome().getName();
				String away = match.getAway().getName();
				long kickoffTime = match.getKickoffTime();
				String name = match.getName();

				Tournament8xResponse.Round round = match.getRound();
				MatchModel matchModel = MatchModel.builder()
						.home(home)
						.away(away)
						.round(round)
						.kickoffTime(convertUnixTimestampToLocalDateTime(kickoffTime))
						.tnName(tnName)
						.name(name)
						.build();
				matchesOfTour.add(matchModel);
			});
		});

		matchRepository.saveAll(matchConverter.convertToMatchEntity(matchesOfTour));
		return null;
	}




	private LocalDateTime convertUnixTimestampToLocalDateTime(long unixTimestamp) {
		// Convert the Unix timestamp to an Instant
		Instant instant = Instant.ofEpochSecond(unixTimestamp);
		// Convert the Instant to a LocalDateTime using the system default time zone
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
}
