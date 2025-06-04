package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
	private String name;
	private String slug;
	private String shortName;
	private String gender;
	private SportResponse sport;
	private Integer userCount;
	private String nameCode;
	private Boolean disabled;
	private Boolean national;
	private Integer type;
	private Integer id;
	private CountryResponse country;
	private TeamColorsResponse teamColors; // Thêm trường mới
	private FieldTranslationsResponse fieldTranslations; // Thêm trường mới
//	private SubTeamsResponse subTeams; // Thêm trường mới
}
