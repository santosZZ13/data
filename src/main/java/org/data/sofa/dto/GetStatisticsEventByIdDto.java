package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface GetStatisticsEventByIdDto {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private Integer id;
		private String from;
		private String to;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response {
		private Statistics statistics;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Statistics {
		@JsonProperty("total_matches")
		private Integer totalMatches;
		private Integer averageGoalPerMach;
		private Integer averageGoalInFirstHalf;
		private Integer averageGoalInSecondHalf;
		private ScoreDetail scoreInFirstHalf;
		private ScoreDetail scoreInSecondHalf;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScoreDetail {
		private Integer count;
		private Integer ratio;
	}
}
