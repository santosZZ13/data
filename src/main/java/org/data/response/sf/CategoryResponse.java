package org.data.response.sf;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryResponse {
	private String name;
	private String slug;
	private SportResponse sport;
	private Integer id;
	private CountryResponse country;
	private String flag;
	private String alpha2;
}
