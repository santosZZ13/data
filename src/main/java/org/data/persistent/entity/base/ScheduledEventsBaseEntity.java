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
	private Boolean hasGlobalHighlights;
	private Boolean hasEventPlayerStatistics;
	private Boolean hasEventPlayerHeatMap;
	private Integer detailId;
	private Boolean crowdsourcingDataDisplayEnabled;
	private Integer idEvent;
	private Boolean crowdsourcingEnabled;
	private LocalDateTime startTimestamp;
	private String slug;
	private Boolean finalResultOnly;
	private Boolean feedLocked;
	private Boolean isEditor;
}
