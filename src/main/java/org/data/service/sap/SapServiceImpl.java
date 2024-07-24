package org.data.service.sap;

import lombok.AllArgsConstructor;
import org.data.properties.ConnectionProperties;
import org.data.util.RestConnector;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class SapServiceImpl implements SapService {

	private final RestConnector restConnector;

	@Override
	public <T> T restSofaScoreGet(String requestPath, Class<T> response) {
		return restConnector.restGet(ConnectionProperties.Host.SOFASCORE, requestPath, response);
	}

	@Override
	public <T> T restLocalGet(String requestPath, Class<T> response) {
		return restConnector.restGet(ConnectionProperties.Host.LOCAL, requestPath, response);
	}


	@Override
	public <T> T restEightXBetGet(String requestPath, Class<T> response) {
		return restConnector.restGet(ConnectionProperties.Host.EIGHTXBET, requestPath, response);
	}


	@Override
	public <T> T restEightXBetGet(String requestPath, Map<String, Object> queryParams, Class<T> response) {
		return restConnector.restGet(ConnectionProperties.Host.EIGHTXBET, requestPath, queryParams, response);
	}

}