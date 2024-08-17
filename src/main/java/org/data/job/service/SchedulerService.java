package org.data.job.service;

import org.data.common.model.BaseResponse;
import org.data.job.dto.JobDTO;

public interface SchedulerService {
	BaseResponse createJob(JobDTO.Request request);

	BaseResponse getJobs();

	BaseResponse deleteJob(String jobName);
}
