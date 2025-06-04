package org.data.response.sf.child;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeasonResponse {
	private String name;
	private String year;
	private Boolean editor;
	private Integer id;
}
