package org.data;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
@AllArgsConstructor
public class App implements CommandLineRunner {


	private final RedisTemplate<Object, Object> redisTemplate;
//	public static class LoggingInterceptor implements ClientHttpRequestInterceptor {
//		@Override
//		public @NotNull ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
//			System.out.println("Request Headers: " + request.getHeaders());
//			System.out.println("Request URI: " + request.getURI());
//			ClientHttpResponse response = execution.execute(request, body);
//			System.out.println("Response Status: " + response.getStatusCode());
//			return response;
//		}
//	}
	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);
//		RestTemplate restTemplate = new RestTemplate();
//		restTemplate.getInterceptors().add(new LoggingInterceptor());
//		String url = "https://001xm4z27x2jy1u-api.pj51m7rx.com/product/business/sport/prematch/tournament?sid=1&sort=tournament&inplay=false&date=20240804";
//
//		// Set the headers
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("User-Agent", "Mozilla/5.0");
//		headers.add("referer", "https://8xbet00.cc/");
//		headers.add("x-checksum", "13d730b032dda17d6a440904f27560d7df77cd0b20e56a78153be8ade717e129");
//		headers.add("Cookie", "_cfuvid=5H_RaBFYbOsm7XPrammmJeRkvhYXOUwa9ukAN6EdN7A-1722751072280-0.0.1.1-604800000");
//
////		headers.add("accept", "application/json, text/plain, */*");
////		headers.add("accept-language", "en-US,en;q=0.9");
////		headers.add("origin", "https://8xbet00.cc");
////		headers.add("sec-fetch-dest", "empty");
////		headers.add("sec-fetch-mode", "cors");
////		headers.add("sec-fetch-site", "cross-site");
////		headers.add("time-zone", "GMT+07:00");
//		// Build the HttpEntity with headers
//		HttpEntity<String> entity = new HttpEntity<>(headers);
//
//		// Make the GET request
//		try {
//			ResponseEntity<String> response = restTemplate.exchange(
//					url,
//					HttpMethod.GET,
//					entity,
//					String.class
//			);
//			System.out.println(response.getBody());
//		} catch (HttpClientErrorException e) {
//			System.err.println("Request failed with status code: " + e.getStatusCode());
//			System.err.println("Response body: " + e.getResponseBodyAsString());
//			System.out.println(e.getMessage());
//		}
	}

	@Override
	public void run(String... args) throws Exception {
		redisTemplate.opsForValue().set("test", "Java", 5, TimeUnit.SECONDS);
		System.out.println(redisTemplate.opsForValue().get("Hello"));
		redisTemplate.execute((RedisCallback<? extends Object>)  connection ->  {
			connection.hashCommands().hSet()
		})
	}
}
