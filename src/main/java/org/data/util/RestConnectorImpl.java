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


	@Override
	public <T> T restGet(ConnectionProperties.Host host, String requestPath, Class<T> responseType) {
		return null;
	}

	@Override
	public <T> T restGet(ConnectionProperties.Host host, String requestPath, Map<String, ?> queryParams, Class<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<?> entity = new HttpEntity<>(headers);

		return restTemplate.exchange(
				"",
				HttpMethod.GET,
				entity,
				responseType,
				queryParams
		).getBody();
	}


	private String buildUrl(String url, String requestPath, Map<String, Object> queryParams) {

		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
		uriComponentsBuilder.path(requestPath);


	}
}
