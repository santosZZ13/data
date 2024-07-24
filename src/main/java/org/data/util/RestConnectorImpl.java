package org.data.util;

import lombok.AllArgsConstructor;
import org.data.properties.ConnectionProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
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
		Map<String, String> headers = connectionPropertiesHost.getHeaders();
		String url = connectionPropertiesHost.getUrl();


		HttpHeaders httpHeaders = new HttpHeaders();
		headers.forEach(httpHeaders::set);
		httpHeaders.set("User-Agent", "PostmanRuntime/7.40.0");


		HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

		return restTemplate.exchange(
				buildUrl(url, requestPath, queryParams),
				HttpMethod.GET,
				entity,
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
		return restTemplate.getForObject(urlWithArgs, response);
	}

	private String buildUrlWithArgs(String url, String requestPath, List<Integer> args) {
		String[] split = requestPath.split("/");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			if (split[i].contains("{}")) {
				sb.append(args.get(0));
				args.remove(0);
			} else {
				sb.append(split[i]);
			}
		}
		return url + sb;
	}

	private String buildUrl(String url, String requestPath, Map<String, ?> queryParams) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url + requestPath);
		queryParams.forEach(uriComponentsBuilder::queryParam);
		return uriComponentsBuilder.toUriString();
	}
}
