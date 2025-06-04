package org.data.util.request;

//TODO: upgrade restTemplate to Webclient
public class WebClientTemplate {
//	private final WebClient webClient;

//	public Mono<T> execute(String path, HttpMethod method, HttpHeaders headers, Object body, Class<T> responseType) {
//		return webClient.method(method)
//				.uri(path)
//				.headers(h -> h.addAll(headers))
//				.bodyValue(body != null ? body : "")
//				.retrieve()
//				.onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
//						.flatMap(errorBody -> Mono.error(new ApiException(
//								"API request failed",
//								response.statusCode().value(),
//								errorBody))))
//				.bodyToMono(responseType);
//	}
}
