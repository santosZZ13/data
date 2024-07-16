package org.data.persistent.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledEventsBaseEntity {
	private boolean hasGlobalHighlights;
	private boolean hasEventPlayerStatistics;
	private boolean hasEventPlayerHeatMap;
	private int detailId;
	private boolean crowdsourcingDataDisplayEnabled;
	private int idEvent;
	private boolean crowdsourcingEnabled;
	private LocalDateTime startTimestamp;
	private String slug;
	private boolean finalResultOnly;
	private boolean feedLocked;
	private boolean isEditor;
}
