package org.data.tournament.persistent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ScheduledEventsCommon {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class UniqueTournament {
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
