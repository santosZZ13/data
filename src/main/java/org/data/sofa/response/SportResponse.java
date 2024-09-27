package org.data.sofa.response;

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
