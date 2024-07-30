package org.data.persistent.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "history_fetch_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryFetchEventEntity {
	private String id;
	private int idTeam;
	private Long timeElapsed;
	private Integer total;
//	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@Field("created_date")
	private LocalDateTime createdDate;
	@Field("updated_date")
	private LocalDateTime updatedDate;
}
