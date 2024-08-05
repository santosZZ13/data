package org.data.eightBet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EightXBetEventsResponse {
	private String msg;
	private int code;
	private Data data;

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Data {
		private List<EightXBetTournamentResponse> tournaments;
	}

	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class EightXBetTournamentResponse {
		private Integer sid;
		private Integer tid;
		private Integer cid;
		private String name;
		private Boolean favorite;
		private Integer priority;
		private Integer count;
		private List<EightXBetCommonResponse.EightXBetMatchResponse> matches;
	}

}
