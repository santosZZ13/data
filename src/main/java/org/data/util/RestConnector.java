package org.data.util;

import org.data.properties.ConnectionProperties;

import java.util.List;
import java.util.Map;

public interface RestConnector {
	<T> T restGet(ConnectionProperties.Host host, String requestPath, Class<T> responseType);
	<T> T restGet(ConnectionProperties.Host host, String requestPath, Map<String, ?> queryParams, Class<T> response);

	<T> T restGet(ConnectionProperties.Host host, String requestPath, Class<T> response, List<Integer> args);
}
