package org.data.job.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface JobDTO {

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Request {
		private Job job;
	}

	@Builder
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class Job {
		private String jobName;
		private String requestPath;
		private String baseURL;
		private String cronExpression;
	}
}
