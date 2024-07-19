package org.data.job.controller;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.job.dto.JobDTO;
import org.data.job.service.SchedulerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SchedulerController {

	private final SchedulerService schedulerService;

	@GetMapping("/api/data-service/scheduler")
	public GenericResponseWrapper createJob(@RequestBody JobDTO.Job request) {
		return schedulerService.createJob(request);
	}
}
