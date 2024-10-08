package org.data.response.sf;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SportResponse {
	private String name;
	private String slug;
	private Integer id;
}
