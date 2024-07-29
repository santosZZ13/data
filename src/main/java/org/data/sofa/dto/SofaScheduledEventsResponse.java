package org.data.sofa.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class SofaScheduledEventsResponse {

	private List<EventResponse> events;
	private Boolean hasNextPage;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class EventResponse {
		private ScheduledEventsCommonResponse.TournamentResponse tournament;
		private ScheduledEventsCommonResponse.SeasonResponse season;
		private ScheduledEventsCommonResponse.RoundInfo roundInfo;
		private String customId;
		private ScheduledEventsCommonResponse.Status status;
		private Integer winnerCode;
		private ScheduledEventsCommonResponse.TeamResponse homeTeam;
		private ScheduledEventsCommonResponse.TeamResponse awayTeam;
		private ScheduledEventsCommonResponse.Score homeScore;
		private ScheduledEventsCommonResponse.Score awayScore;
		private ScheduledEventsCommonResponse.Time time;
		private ScheduledEventsCommonResponse.Changes changes;
		private Boolean hasGlobalHighlights;
		private Boolean hasEventPlayerStatistics;
		private Boolean hasEventPlayerHeatMap;
		private Integer detailId;
		private Boolean crowdsourcingDataDisplayEnabled;
		private Integer id;
		private Boolean crowdsourcingEnabled;
		private Long startTimestamp;
		private String slug;
		private Boolean finalResultOnly;
		private Boolean feedLocked;
		private Boolean isEditor;

	}
}
