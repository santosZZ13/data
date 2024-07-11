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
		private TournamentResponse tournament;
		@NotNull
		private SeasonResponse season;
		@Nullable
		private RoundInfo roundInfo;
		@NotNull
		private String customId;
		@NotNull
		private Status status;
		@Nullable
		private Integer winnerCode;
		@NotNull
		private Team homeTeam;
		@NotNull
		private Team awayTeam;
		@Nullable
		private Score homeScore;
		@Nullable
		private Score awayScore;
		@NotNull
		private Time time;
		@NotNull
		private Changes changes;
		private boolean hasGlobalHighlights;
		private boolean hasEventPlayerStatistics;
		private boolean hasEventPlayerHeatMap;
		private int detailId;
		private boolean crowdsourcingDataDisplayEnabled;
		private int id;
		private boolean crowdsourcingEnabled;
		private long startTimestamp;
		@NotNull
		private String slug;
		private boolean finalResultOnly;
		private boolean feedLocked;
		private boolean isEditor;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Nullable
	public static class Score {
		@Nullable
		private Integer current;
		@Nullable
		private Integer display;
		@Nullable
		private Integer period1;
		@Nullable
		private Integer period2;
		@Nullable
		private Integer normaltime;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentResponse {
		@NotNull
		private String name;
		@NotNull
		private String slug;
		@NotNull
		private ScheduledEventsCommonResponse.Category category;
		@NotNull
		private ScheduledEventsCommonResponse.UniqueTournament uniqueTournament;
		private int priority;
		private int id;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class SeasonResponse {
		@Nullable
		private String name;
		@Nullable
		private String year;
		@Nullable
		private Boolean editor;
		@Nullable
		private Integer id;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class RoundInfo {
		@Nullable
		private Integer round;
		@Nullable
		private String name;
		@Nullable
		private Integer cupRoundType;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class Team {
		@NotNull
		private String name;
		@NotNull
		private String slug;
		@NotNull
		private String shortName;
		private ScheduledEventsCommonResponse.Sport sport;
		private int userCount;
		@NotNull
		private String nameCode;
		private boolean disabled;
		private boolean national;
		private int type;
		private int id;
		@NotNull
		private ScheduledEventsCommonResponse.Country country;
		@Nullable
		private List<Team> subTeams;
		@NotNull
		private ScheduledEventsCommonResponse.TeamColors teamColors;
		@Nullable
		private ScheduledEventsCommonResponse.FieldTranslations fieldTranslations;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Status {
		private int code;
		@NotNull
		private String description;
		@NotNull
		private String type;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Time {
		private String injuryTime1;
		private String injuryTime2;
		private long currentPeriodStartTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	public static class Changes {
		private List<String> changes;
		private long changeTimestamp;
	}

}
