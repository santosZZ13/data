package org.data.job.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.job.dto.JobDTO;
import org.data.job.service.SchedulerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SchedulerController {

	private final SchedulerService schedulerService;

	@PostMapping("/api/data-service/scheduler")
	public GenericResponseWrapper createJob(@RequestBody JobDTO.Request request) {
		return schedulerService.createJob(request);
	}

	@GetMapping("/api/data-service/scheduler")
	public GenericResponseWrapper getJobs() {
		return schedulerService.getJobs();
	}

	@GetMapping("/api/data-service/scheduler/{jobName}")
	public GenericResponseWrapper deleteJob(@PathVariable String jobName) {
		return schedulerService.deleteJob(jobName);
	}
}
