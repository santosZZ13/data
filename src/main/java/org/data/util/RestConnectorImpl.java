package org.data.util;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.properties.ConnectionProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class RestConnectorImpl implements RestConnector {

	private final RestTemplate restTemplate;
	private final ConnectionProperties connectionProperties;


	@Override
	public <T> T restGet(ConnectionProperties.Host host, String requestPath, Class<T> responseType) {

		ConnectionProperties.ServerConnection connectionPropertiesHost = connectionProperties.getHost(host);
		Map<String, String> headers = connectionPropertiesHost.getHeaders();
		String url = connectionPropertiesHost.getUrl();
		HttpHeaders httpHeaders;
		HttpEntity<?> entity = null;
		if (headers != null) {
			httpHeaders = new HttpHeaders();
			headers.forEach(httpHeaders::set);
			entity = new HttpEntity<>(httpHeaders);
		}

		return restTemplate.exchange(
				url + requestPath,
				HttpMethod.GET,
				entity,
				responseType
		).getBody();
	}

	@Override
	public <T> T restGet(ConnectionProperties.Host host, String requestPath, Map<String, ?> queryParams, Class<T> responseType) {
		ConnectionProperties.ServerConnection connectionPropertiesHost = connectionProperties.getHost(host);
		String url = connectionPropertiesHost.getUrl();
		HttpEntity<?> httpEntity = buildEntity(connectionPropertiesHost.getHeaders());

		return restTemplate.exchange(
				buildUrl(url, requestPath),
				HttpMethod.GET,
				httpEntity,
				responseType,
				queryParams
		).getBody();
	}

	//	String SCHEDULED_EVENT_TEAM_NEXT = "/team/{}/events/next/{}";
	// fill args in the requestPath
	@Override
	public <T> T restGet(ConnectionProperties.Host host, String requestPath, Class<T> response, List<Integer> args) {
		if (args.isEmpty()) {
			restGet(host, requestPath, response);
		}
		String url = connectionProperties.getHost(host).getUrl();
		String urlWithArgs = buildUrlWithArgs(url, requestPath, args);
		log.info("#restGet calling host: {} with url: {}", host, urlWithArgs);
		return restTemplate.getForObject(urlWithArgs, response);
	}

	public String buildUrlWithArgs(String url, String requestPath, List<Integer> args) {
		String[] split = requestPath.split("/");
		long count = Arrays.stream(split).filter(s -> s.contains("{}")).count();

		if (count != args.size()) {
			throw new IllegalArgumentException("Number of arguments does not match the number of placeholders");
		}

		int index = 0;
		StringBuilder sb = new StringBuilder();
		for (String s : split) {
			if (s.contains("{}")) {
				sb.append("/").append(args.get(index));
				index++;
			} else {
				if (s.isEmpty()) {
					continue;
				}
				sb.append("/").append(s);
			}
		}
		return url + sb;
	}

	private HttpEntity<?> buildEntity(Map<String, String> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();
//		headers.forEach(httpHeaders::set);

		httpHeaders.add("User-Agent", "Mozilla/5.0");
		httpHeaders.add("Referer", "https://8xbet00.cc/");
		httpHeaders.add("X-checksum", "13d730b032dda17d6a440904f27560d7df77cd0b20e56a78153be8ade717e129");
		httpHeaders.add("Cookie", "_cfuvid=z6NLQRvcNNxBocPXfmnJzf0sOGDMWjzgJaycuctJy4c-1722655062489-0.0.1.1-604800000");


		return new HttpEntity<>(httpHeaders);
	}

	private String buildUrl(String url, String requestPath) {
		return UriComponentsBuilder
				.fromHttpUrl(url + requestPath)
				.queryParam("sid", "{sid}")
				.queryParam("sort", "{sort}")
				.queryParam("inplay", "{inplay}")
				.queryParam("date", "{date}")
				.encode()
				.toUriString();
	}
}
