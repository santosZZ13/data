package org.data.config;

import org.data.converter.ZonedDateTimeReadConverter;
import org.data.converter.ZonedDateTimeWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class Converters {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {
		return new MongoCustomConversions(
				Arrays.asList(
						new ZonedDateTimeReadConverter(),
						new ZonedDateTimeWriteConverter())
		);
	}
}