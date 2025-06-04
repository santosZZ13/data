package org.data.util.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtil {
	public static HttpHeaders createDefaultHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + apiKey);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

	/**
	 * Tạo query string từ Map
	 *
	 * @param params - Map chứa các query param
	 * @return Query string (ví dụ: ?key1=value1&key2=value2)
	 */
	public static String createQueryString(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		return "?" + params.entrySet().stream()
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&"));
	}

	/**
	 * Tạo Map query param
	 *
	 * @return Map rỗng để thêm param
	 */
	public static Map<String, String> createQueryParams() {
		return new HashMap<>();
	}
}
