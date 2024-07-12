package org.data.tournament.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ScheduledEventsCommonResponse {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TournamentResponse {
		@NotNull
		private String name;
		@NotNull
		@JsonIgnore
		private String slug;
		@NotNull
		private ScheduledEventsCommonResponse.Category category;
		@NotNull
		@JsonIgnore
		private ScheduledEventsCommonResponse.UniqueTournament uniqueTournament;

		@JsonIgnore
		private int priority;

		@JsonIgnore
		private int id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class UniqueTournament {
		@NotNull
		private String name;
		@NotNull
		private String slug;
		private ScheduledEventsCommonResponse.Category category;
		private int userCount;
		private boolean crowdsourcingEnabled;
		private boolean hasPerformanceGraphFeature;
		private int id;
		private boolean hasEventPlayerStatistics;
		private boolean displayInverseHomeAwayTeams;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Category {
		@NotNull
		private String name;
		@NotNull
		@JsonIgnore
		private String slug;
		@Nullable
		private ScheduledEventsCommonResponse.Sport sport;
		private Integer id;
		@Nullable
		private ScheduledEventsCommonResponse.Country country;
		@Nullable
		private String flag;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Sport {
		@Nullable
		private String name;
		@Nullable
		private String slug;
		private Integer id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Country {
		@Nullable
		private String alpha2;
		@Nullable
		private String alpha3;
		@Nullable
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class TeamColors {
		@Nullable
		private String primary;
		@Nullable
		private String secondary;
		@Nullable
		private String text;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class FieldTranslations {
		@Nullable
		private ScheduledEventsCommonResponse.Translations nameTranslation;
		@Nullable
		private ScheduledEventsCommonResponse.Translations shortNameTranslation;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Translations {
		@Nullable
		private String ar;
		@Nullable
		private String ru;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class SeasonResponse {
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
	class RoundInfo {
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
	class Team {
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
	@Nullable
	class Score {
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
	class Status {
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
	class Time {
		private String injuryTime1;
		private String injuryTime2;
		private long currentPeriodStartTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Changes {
		private List<String> changes;
		private long changeTimestamp;
	}
}
