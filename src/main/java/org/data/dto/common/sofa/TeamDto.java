package org.data.dto.common.sofa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
	private String id;
	private SofaTeamDto sofaTeamDto;
	private ExBetTeamDto exBetTeamDto;

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SofaTeamDto {
		private Integer teamId;
		private String name;
		private String country;
		private String normalizedName;
		private String shortName;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ExBetTeamDto {
		private Integer teamId;
		private String name;
		private String country;
		private String normalizedName;
		private String shortName;
	}
}
