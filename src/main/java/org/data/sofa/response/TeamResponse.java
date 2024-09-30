package org.data.sofa.response;

import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamResponse {
	private String name;
	private String slug;
	private String shortName;
	private SportResponse sport;
	private Integer userCount;
	private String nameCode;
	private Boolean disabled;
	private Boolean national;
	private Integer type;
	private Integer id;
	private CountryResponse country;
	private List<TeamResponse> subTeamResponses;
	private TeamColorsResponse teamColors;
	private FieldTranslationsResponse fieldTranslations;

}
