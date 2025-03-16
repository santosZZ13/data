package org.data.response;

import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SofaResponse {
	private List<MatchResponse> events;
}
