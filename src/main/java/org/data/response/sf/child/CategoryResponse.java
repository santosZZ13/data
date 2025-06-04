package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
	private Integer id;
	private CountryResponse country;
	private String name;
	private String slug;
	private SportResponse sport;
	private String flag;
	private String alpha2;
	private FieldTranslationsResponse fieldTranslations;
}