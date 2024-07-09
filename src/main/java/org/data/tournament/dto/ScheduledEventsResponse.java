package org.data.tournament.dto;

import lombok.*;

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
	public static class Event {
		private TournamentResponse tournament;
		private Season season;
		private RoundInfo roundInfo;
		private String customId;
		private Status status;
		private Team homeTeam;
		private Team awayTeam;
		private Score homeScore;
		private Score awayScore;
		private Time time;
		private Changes changes;
		private boolean hasGlobalHighlights;
		private int detailId;
		private boolean crowdsourcingDataDisplayEnabled;
		private int id;
		private boolean crowdsourcingEnabled;
		private long startTimestamp;
		private String slug;
		private boolean finalResultOnly;
		private boolean feedLocked;
		private boolean isEditor;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Score {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normaltime;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TournamentResponse {
		private String name;
		private String slug;
		private ScheduledEventsCommonResponse.Category category;
		private ScheduledEventsCommonResponse.UniqueTournament uniqueTournament;
		private int priority;
		private int id;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Season {
		private String name;
		private String year;
		private boolean editor;
		private int id;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class RoundInfo {
		private int round;
		private String name;
		private int cupRoundType;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Team {
		private String name;
		private String slug;
		private String shortName;
		private ScheduledEventsCommonResponse.Sport sport;
		private int userCount;
		private String nameCode;
		private boolean disabled;
		private boolean national;
		private int type;
		private int id;
		private ScheduledEventsCommonResponse.Country country;
		private List<Team> subTeams;
		private ScheduledEventsCommonResponse.TeamColors teamColors;
		private ScheduledEventsCommonResponse.FieldTranslations fieldTranslations;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Status {
		private int code;
		private String description;
		private String type;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Time {
		private String currentPeriodStartTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Changes {
		private long changeTimestamp;
	}

}
