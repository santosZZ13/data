package org.data.sofa.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UniqueTournamentResponse {
	private String name;
	private String slug;
	private String primaryColorHex;
	private String secondaryColorHex;
	private CategoryResponse category;
	private Integer userCount;
	private Boolean crowdsourcingEnabled;
	private Boolean hasPerformanceGraphFeature;
	private Integer id;
	private Boolean hasEventPlayerStatistics;
	private Boolean displayInverseHomeAwayTeams;
}
