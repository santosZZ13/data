package org.data.dto.analysis;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.data.common.model.BaseResponse;

import java.util.List;

public interface GetAnalysisDto {
	@EqualsAndHashCode(callSuper = true)
	@SuperBuilder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Response extends BaseResponse {
		private ResponseData data;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public class ResponseData {
		private int total;
		private List<AnalysisResponse> analysis;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class AnalysisResponse {
		private int stt;
		private String tournament;
		private String home;
		private String away;
		private String time;
		private double over15Index;
		private double over05Index;
	}
}
