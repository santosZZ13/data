package org.data.util.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class RestHttpClient {
	private final RestTemplate restTemplate;

}
