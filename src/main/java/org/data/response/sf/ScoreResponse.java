package org.data.response.sf;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreResponse {
	private Integer current;
	private Integer display;
	@JsonPropertyDescription("Half Time")
	private Integer period1;
	@JsonPropertyDescription("Second Half")
	private Integer period2;
	@JsonPropertyDescription("Full Time")
	private Integer normaltime;
	@JsonPropertyDescription("Extra Time 1st Half")
	private Integer extra1;
	@JsonPropertyDescription("Extra Time 2nd Half")
	private Integer extra2;
	@JsonPropertyDescription("Overtime")
	private Integer overtime;
	private Integer penalties;
	private Boolean scoreEmpty;
}
