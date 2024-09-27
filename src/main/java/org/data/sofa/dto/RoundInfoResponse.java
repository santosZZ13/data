package org.data.sofa.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RoundInfoResponse {
	private Integer round;
	private String name;
	private Integer cupRoundType;
}
