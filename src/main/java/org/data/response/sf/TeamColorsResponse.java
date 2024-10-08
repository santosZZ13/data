package org.data.response.sf;

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
