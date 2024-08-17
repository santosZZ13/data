package org.data.sofa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;

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

	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private GetStatisticsEventData data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class GetStatisticsEventData {
		private Statistics statistics;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Statistics {
		@JsonProperty("total_matches")
		private int totalMatches;
		private float averageGoalPerMach;
		private float averageGoalInFirstHalf;
		private float averageGoalInSecondHalf;
		private ScoreDetail scoreInFirstHalf;
		private ScoreDetail scoreInSecondHalf;

	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class ScoreDetail {
		private int count;
		private String ratio;
	}
}
