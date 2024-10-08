package org.data.response.ex;

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
public class ExBetResponse {
	private String msg;
	private int code;
	private Data data;


	@Builder
	@lombok.Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Data {
		private List<ExBetTournamentResponse> tournaments;
	}
}
