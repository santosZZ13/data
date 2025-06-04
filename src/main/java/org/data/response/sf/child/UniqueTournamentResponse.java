package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniqueTournamentResponse {
	private String name;
	private String slug;
	private CategoryResponse category;
	private Integer userCount;
	private Boolean hasPerformanceGraphFeature;
	private Integer id;
	private Boolean hasEventPlayerStatistics;
	private Boolean displayInverseHomeAwayTeams;
	private FieldTranslationsResponse fieldTranslations;
}
