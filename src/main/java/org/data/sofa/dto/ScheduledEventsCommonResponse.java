package org.data.sofa.dto;

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
		private String name;
		private String slug;
		private ScheduledEventsCommonResponse.Category category;
		private ScheduledEventsCommonResponse.UniqueTournament uniqueTournament;
		private Integer priority;
		private Integer id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class UniqueTournament {
		private String name;
		private String slug;
		private ScheduledEventsCommonResponse.Category category;
		private Integer userCount;
		private Boolean crowdsourcingEnabled;
		private Boolean hasPerformanceGraphFeature;
		private Integer id;
		private Boolean hasEventPlayerStatistics;
		private Boolean displayInverseHomeAwayTeams;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Category {
		private String name;
		private String slug;
		private ScheduledEventsCommonResponse.SportResponse sport;
		private Integer id;
		private ScheduledEventsCommonResponse.Country country;
		private String flag;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class SportResponse {
		private String name;
		private String slug;
		private Integer id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Country {
		private String alpha2;
		private String alpha3;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class TeamColors {
		private String primary;
		private String secondary;
		private String text;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class FieldTranslations {
		private ScheduledEventsCommonResponse.Translations nameTranslation;
		private ScheduledEventsCommonResponse.Translations shortNameTranslation;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Translations {
		private String ar;
		private String ru;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class SeasonResponse {
		private String name;
		private String year;
		private Boolean editor;
		private Integer id;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class RoundInfo {
		private Integer round;
		private String name;
		private Integer cupRoundType;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class TeamResponse {
		private String name;
		private String slug;
		private String shortName;
		private SportResponse sportResponse;
		private Integer userCount;
		@NotNull
		private String nameCode;
		private Boolean disabled;
		private Boolean national;
		private Integer type;
		private Integer id;
		@NotNull
		private ScheduledEventsCommonResponse.Country country;
		@Nullable
		private List<TeamResponse> subTeamResponses;
		@NotNull
		private ScheduledEventsCommonResponse.TeamColors teamColors;
		@Nullable
		private ScheduledEventsCommonResponse.FieldTranslations fieldTranslations;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Score {
		private Integer current;
		private Integer display;
		private Integer period1;
		private Integer period2;
		private Integer normaltime;
		private Integer extra1;
		private Integer extra2;
		private Integer overtime;
		private Integer penalties;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Status {
		private Integer code;
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
		private Long currentPeriodStartTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@ToString
	class Changes {
		private List<String> changes;
		private Long changeTimestamp;
	}
}
