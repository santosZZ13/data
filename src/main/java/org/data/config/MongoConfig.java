package org.data.config;

import org.data.persistent.entity.base.BaseEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import java.time.LocalDateTime;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

	@Bean
	public AbstractMongoEventListener<Object> auditingMongoEventListener() {
		return new AbstractMongoEventListener<Object>() {

			@Override
			public void onBeforeConvert(@NotNull BeforeConvertEvent<Object> event) {
				Object source = event.getSource();
				System.out.println("onBeforeConvert: Source class = " + source.getClass().getName());
				if (source instanceof BaseEntity baseEntity) {
					if (baseEntity.getCreatedAt() == null) {
						baseEntity.setCreatedAt(LocalDateTime.now());
					}
					baseEntity.setUpdatedAt(LocalDateTime.now());
				}
			}

			@Override
			public void onBeforeSave(@NotNull BeforeSaveEvent<Object> event) {
				Object source = event.getSource();
				System.out.println("onBeforeSave: Source class = " + source.getClass().getName());
				if (source instanceof BaseEntity baseEntity) {
					if (baseEntity.getCreatedAt() == null) {
						baseEntity.setCreatedAt(LocalDateTime.now());
					}
					baseEntity.setUpdatedAt(LocalDateTime.now());
				}
			}
		};
	}
}
