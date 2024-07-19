package org.data.util;

import lombok.AllArgsConstructor;
import org.data.properties.ConnectionProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
@AllArgsConstructor
public class RestConnectorImpl implements RestConnector {

	private final RestTemplate restTemplate;
	private final ConnectionProperties connectionProperties;

	@Override
	public <T> T restGet(ConnectionProperties.Host host, String requestPath, Class<T> responseType) {
		return null;
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








	private String buildUrl(String url, String requestPath, Map<String, ?> queryParams) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url + requestPath);
		queryParams.forEach(uriComponentsBuilder::queryParam);
		return uriComponentsBuilder.toUriString();
	}
}
