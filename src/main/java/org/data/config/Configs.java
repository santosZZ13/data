package org.data.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScans({@ComponentScan("org.data.job")})
public class Configs {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
			request.getHeaders().add("user-agent", "Mozilla/5.0");
			request.getHeaders().add("referer", "https://8xbet00.cc/");
			request.getHeaders().add("x-checksum", "13d730b032dda17d6a440904f27560d7df77cd0b20e56a78153be8ade717e129");
			return execution.execute(request, body);
		};
		return restTemplateBuilder.additionalInterceptors(interceptor).build();
	}
}
