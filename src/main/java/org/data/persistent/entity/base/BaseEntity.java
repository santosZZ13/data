package org.data.persistent.entity.base;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {

	@CreatedDate
	@Field("created_at")
	public ZonedDateTime createdAt;

	@LastModifiedDate
	@Field("updated_at")
	public ZonedDateTime updatedAt;

}
