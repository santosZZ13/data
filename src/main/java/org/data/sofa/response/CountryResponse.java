package org.data.sofa.response;

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
