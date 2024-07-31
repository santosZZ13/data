package org.data.persistent.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "history_fetch_event")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryFetchEventEntity {
	private String id;
	@Field("team_id")
	private Integer teamId;
	@Field("time_elapsed")
	private Long timeElapsed;
	@Field("total")
	private Integer total;
	//	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Field("created_date")
	private LocalDateTime createdDate;
	@Field("updated_date")
	private LocalDateTime updatedDate;
}
