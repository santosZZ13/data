package org.data.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
	static class Event {
		private Tournament tournament;
		private Season season;
		private RoundInfo roundInfo;
		private String customId;
		private Status status;
		private Team homeTeam;
		private Team awayTeam;
		private Map<String, Object> homeScore;
		private Map<String, Object> awayScore;
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
	static class Tournament {
		private String name;
		private String slug;
		private Category category;
		private UniqueTournament uniqueTournament;
		private int priority;
		private int id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Category {
		private String name;
		private String slug;
		private Sport sport;
		private int id;
		private Country country;
		private String flag;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Sport {
		private String name;
		private String slug;
		private int id;
	}



	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Season {
		private String name;
		private String year;
		private boolean editor;
		private int id;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class RoundInfo {
		private int round;
		private String name;
		private int cupRoundType;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Team {
		private String name;
		private String slug;
		private String shortName;
		private Sport sport;
		private int userCount;
		private String nameCode;
		private boolean disabled;
		private boolean national;
		private int type;
		private int id;
		private Country country;
		private List<Team> subTeams;
		private TeamColors teamColors;
		private FieldTranslations fieldTranslations;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Country {
		private String alpha2;
		private String alpha3;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Status {
		private int code;
		private String description;
		private String type;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class FieldTranslations {
		private Translations nameTranslation;
		private Translations shortNameTranslation;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Translations {
		private String ar;
		private String ru;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class TeamColors {
		private String primary;
		private String secondary;
		private String text;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Time {
		private String time;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Changes {
		private long changeTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class UniqueTournament {
		private String name;
		private String slug;
		private Category category;
		private int userCount;
		private boolean crowdsourcingEnabled;
		private boolean hasPerformanceGraphFeature;
		private int id;
		private boolean hasEventPlayerStatistics;
		private boolean displayInverseHomeAwayTeams;
	}
}
