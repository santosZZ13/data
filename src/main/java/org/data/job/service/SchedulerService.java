package org.data.job.service;

import org.data.common.model.GenericResponseWrapper;
import org.data.job.dto.JobDTO;

public interface SchedulerService {
	GenericResponseWrapper createJob(JobDTO.Request request);

	GenericResponseWrapper getJobs();

	GenericResponseWrapper deleteJob(String jobName);
}
