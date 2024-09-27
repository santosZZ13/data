package org.data.sofa.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamColorsResponse {
	private String primary;
	private String secondary;
	private String text;
}
