package org.data.response.ex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class ExRoundResponse {
	private String roundType;
	private String roundName;
	private String roundGroup;
	private String roundGroupName;
	private String roundNumber;
}
