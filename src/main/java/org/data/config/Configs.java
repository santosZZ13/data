package org.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScans({@ComponentScan("org.data.job")})
public class Configs {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
