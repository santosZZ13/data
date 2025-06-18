package org.data.util.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.config.ApiConfig;
import org.data.exception.ExternalServiceException;
import org.data.exception.exceptionHandler.ApiException;
import org.data.util.response.ErrorCodeRegistry;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
@AllArgsConstructor
public class RestClient<T> {
	private final RestTemplate restTemplate;
	private final ApiConfig apiConfig;
	private final ObjectMapper objectMapper;

	/**
	 * Gọi HTTP request và parse kết quả thành type T
	 *
	 * @param url          - URL đầy đủ (bao gồm query string nếu có)
	 * @param method       - HTTP method (GET, POST,...)
	 * @param headers      - Header cho request
	 * @param body         - Request body (nếu có)
	 * @param responseType - Class của type T
	 * @return Object type T
	 * @throws org.data.exception.exceptionHandler.ApiException nếu request thất bại
	 */
	public T execute(String url, HttpMethod method, HttpHeaders headers,
					 Object body, Class<T> responseType) {
		try {
			log.info("Sending {} request to URL: {}", method, url);
			HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new ExternalServiceException(
						"API request failed with status: " + response.getStatusCode(),
						ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR
				);
			}

			if (response.getBody() == null) {
				throw new ExternalServiceException(
						"API response body is null",
						ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR
				);
			}

			log.info("API request successful, parsing response...");
			return objectMapper.readValue(response.getBody(), responseType);

		} catch (RestClientException e) {
			log.error("Error during API request: {}", e.getMessage(), e);
			throw new ExternalServiceException(
					ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR,
					"Failed to execute API request",
					e
			);
		} catch (Exception e) {
			log.error("Error parsing API response: {}", e.getMessage(), e);
			throw new ExternalServiceException(
					ErrorCodeRegistry.EXTERNAL_SERVICE_ERROR,
					"Failed to parse API response",
					e
			);
		}
	}
}
