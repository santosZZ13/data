package org.data.tournament.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledEventsResponse {

	private List<Event> events;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class Event {
		private String idDB;
		@NotNull
		private ScheduledEventsCommonResponse.TournamentResponse tournament;
		@NotNull
		private ScheduledEventsCommonResponse.SeasonResponse season;
		@Nullable
		private ScheduledEventsCommonResponse.RoundInfo roundInfo;
		@NotNull
		private String customId;
		@NotNull
		private ScheduledEventsCommonResponse.Status status;
		@Nullable
		private Integer winnerCode;
		@NotNull
		private ScheduledEventsCommonResponse.Team homeTeam;
		@NotNull
		private ScheduledEventsCommonResponse.Team awayTeam;
		@Nullable
		private ScheduledEventsCommonResponse.Score homeScore;
		@Nullable
		private ScheduledEventsCommonResponse.Score awayScore;
		@NotNull
		private ScheduledEventsCommonResponse.Time time;
		@NotNull
		private ScheduledEventsCommonResponse.Changes changes;
		private boolean hasGlobalHighlights;
		private boolean hasEventPlayerStatistics;
		private boolean hasEventPlayerHeatMap;
		private int detailId;
		private boolean crowdsourcingDataDisplayEnabled;
		private Integer id;
		private boolean crowdsourcingEnabled;
		private long startTimestamp;
		@NotNull
		private String slug;
		private boolean finalResultOnly;
		private boolean feedLocked;
		private boolean isEditor;

	}
}
