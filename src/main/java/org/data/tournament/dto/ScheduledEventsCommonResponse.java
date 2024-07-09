package org.data.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ScheduledEventsCommonResponse {
	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class UniqueTournament {
		private String name;
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
	class Category {
		private String name;
		private String slug;
		private ScheduledEventsCommonResponse.Sport sport;
		private int id;
		private ScheduledEventsCommonResponse.Country country;
		private String flag;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Sport {
		private String name;
		private String slug;
		private int id;
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
		private ScheduledEventsCommonResponse.Translations nameTranslation;
		private ScheduledEventsCommonResponse.Translations shortNameTranslation;
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
