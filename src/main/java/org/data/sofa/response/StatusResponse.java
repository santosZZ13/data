package org.data.sofa.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StatusResponse {
	private Integer code;
	private String description;
	private String type;
}
