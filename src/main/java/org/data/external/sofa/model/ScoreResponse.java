package org.data.external.sofa.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreResponse {
	private Integer current;
	private Integer display;
	@JsonPropertyDescription("Half Time")
	private Integer period1;
	@JsonPropertyDescription("Second Half")
	private Integer period2;
	private Integer period3;
	@JsonPropertyDescription("Full Time")
	@JsonProperty("normaltime")
	private Integer normalTime;
	@JsonPropertyDescription("Extra Time 1st Half")
	private Integer extra1;
	@JsonPropertyDescription("Extra Time 2nd Half")
	private Integer extra2;
	@JsonPropertyDescription("Overtime")
	private Integer overtime;
	private Integer penalties;
	private Boolean scoreEmpty;
	private Integer aggregated;
}
