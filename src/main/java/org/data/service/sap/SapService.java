package org.data.service.sap;

import java.util.Map;

public interface SapService {
	<T> T restSofaScoreGet(String requestPath, Class<T> responseType);
	<T> T restEightXBetGet(String requestPath, Class<T> response);
	<T> T restEightXBetGet(String requestPath, Map<String, Object> queryParams, Class<T> response);

}

