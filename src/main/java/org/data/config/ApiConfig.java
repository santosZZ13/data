package org.data.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class ApiConfig {

	@Value("${api.sofa.base-url}")
	private String sofaBaseUrl;

	@Value("${api.sofa.timeout}")
	private String timeout;
}
