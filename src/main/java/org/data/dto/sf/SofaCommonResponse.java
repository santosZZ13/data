package org.data.dto.sf;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.*;

import java.util.List;

public interface SofaCommonResponse {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TournamentResponse {
		private String name;
		private String slug;
		private SofaCommonResponse.Category category;
		private SofaCommonResponse.UniqueTournament uniqueTournament;
		private Integer priority;
		private Boolean isGroup;
		private Boolean isLive;
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
		private String primaryColorHex;
		private String secondaryColorHex;
		private SofaCommonResponse.Category category;
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
		private SofaCommonResponse.SportResponse sport;
		private Integer id;
		private SofaCommonResponse.Country country;
		private String flag;
		private String alpha2;
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
		private SofaCommonResponse.Translations nameTranslation;
		private SofaCommonResponse.Translations shortNameTranslation;
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
		private String nameCode;
		private Boolean disabled;
		private Boolean national;
		private Integer type;
		private Integer id;
		private SofaCommonResponse.Country country;
		private List<TeamResponse> subTeamResponses;
		private SofaCommonResponse.TeamColors teamColors;
		private SofaCommonResponse.FieldTranslations fieldTranslations;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Score {
		private Integer current;
		private Integer display;
		@JsonPropertyDescription("Half Time")
		private Integer period1;
		@JsonPropertyDescription("Second Half")
		private Integer period2;
		@JsonPropertyDescription("Full Time")
		private Integer normaltime;
		@JsonPropertyDescription("Extra Time 1st Half")
		private Integer extra1;
		@JsonPropertyDescription("Extra Time 2nd Half")
		private Integer extra2;
		@JsonPropertyDescription("Overtime")
		private Integer overtime;
		private Integer penalties;
		private Boolean scoreEmpty;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Status {
		private Integer code;
		private String description;
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
