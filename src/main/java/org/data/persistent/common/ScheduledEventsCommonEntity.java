package org.data.persistent.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledEventsCommonEntity {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TournamentEntity {
		private String name;
		private String slug;
		private ScheduledEventsCommonEntity.Category category;
		private ScheduledEventsCommonEntity.UniqueTournament uniqueTournament;
		private Integer priority;
		private Integer id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SeasonEntity {
		private String name;
		private String year;
		private Boolean editor;
		private Integer id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class StatusEntity {
		private Integer code;
		private String description;
		private String type;
	}


	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamEntity {
		private String name;
		private String slug;
		private String shortName;
		private ScheduledEventsCommonEntity.Sport sport;
		private Integer userCount;
		private String nameCode;
		private Boolean disabled;
		private Boolean national;
		private Integer type;
		private Integer id;
		private ScheduledEventsCommonEntity.Country country;
		private List<TeamEntity> subTeams;
		private ScheduledEventsCommonEntity.TeamColors teamColors;
		private ScheduledEventsCommonEntity.FieldTranslations fieldTranslations;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScoreEntity {
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
	class TimeEntity {
		private String injuryTime1;
		private String injuryTime2;
		private LocalDateTime currentPeriodStartTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ChangesEntity {
		private List<String> changes;
		private LocalDateTime changeTimestamp;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class RoundInfoEntity {
		private Integer round;
		private String name;
		private Integer cupRoundType;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class UniqueTournament {
		private String name;
		private String slug;
		private Category category;
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
	class Category {
		private String name;
		private String slug;
		private Sport sport;
		private Integer id;
		private Country country;
		private String flag;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Sport {
		private String name;
		private String slug;
		private Integer id;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Country {
		private String alpha2;
		private String alpha3;
		private String name;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class TeamColors {
		private String primary;
		private String secondary;
		private String text;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class FieldTranslations {
		private Translations nameTranslation;
		private Translations shortNameTranslation;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Translations {
		private String ar;
		private String ru;
	}
}
