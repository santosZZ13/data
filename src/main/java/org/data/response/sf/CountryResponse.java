package org.data.response.sf;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CountryResponse {
	private String alpha2;
	private String alpha3;
	private String name;
}
