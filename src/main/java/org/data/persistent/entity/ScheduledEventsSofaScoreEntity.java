package org.data.persistent.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.persistent.entity.base.ScheduledEventsBaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scheduled_events_sofa_score")
public class ScheduledEventsSofaScoreEntity  {
	@Id
	private String id;
	private int teamId;
	private String team;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	private List<HistoryEntity> histories;
}
