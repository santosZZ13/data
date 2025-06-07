package org.data.config;

import com.mongodb.MongoClientSettings;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.data.converter.ZonedDateTimeReadConverter;
import org.data.converter.ZonedDateTimeWriteConverter;
import org.data.persistent.entity.base.BaseEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

	private List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

	@Bean
	public AbstractMongoEventListener<Object> auditingMongoEventListener() {
		return new AbstractMongoEventListener<Object>() {

			// This method is called before an entity is converted to a MongoDB document.
			@Override
			public void onBeforeConvert(@NotNull BeforeConvertEvent<Object> event) {
				Object source = event.getSource();
				System.out.println("onBeforeConvert: Source class = " + source.getClass().getName());
				if (source instanceof BaseEntity baseEntity) {
					if (baseEntity.getCreatedAt() == null) {
						baseEntity.setCreatedAt(ZonedDateTime.now());
					}
					baseEntity.setUpdatedAt(ZonedDateTime.now());
				}
			}

			// This method is called before an entity is saved to the database.
			@Override
			public void onBeforeSave(@NotNull BeforeSaveEvent<Object> event) {
				Object source = event.getSource();
				System.out.println("onBeforeSave: Source class = " + source.getClass().getName());
				if (source instanceof BaseEntity baseEntity) {
					if (baseEntity.getCreatedAt() == null) {
						baseEntity.setCreatedAt(ZonedDateTime.now());
					}
					baseEntity.setUpdatedAt(ZonedDateTime.now());
				}
			}
		};
	}
}
