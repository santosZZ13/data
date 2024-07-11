package org.data.tournament.dto;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ScheduledEventsCommonResponse {
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
}
