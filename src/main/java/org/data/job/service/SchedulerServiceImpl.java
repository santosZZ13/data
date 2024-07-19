package org.data.job.service;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.config.FetchEventScheduler;
import org.data.job.dto.JobDTO;
import org.data.properties.ConnectionProperties;
import org.data.util.RestConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Service
@AllArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

	private static final Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	private final RestConnector restConnector;
	private final ConnectionProperties connectionProperties;
	private final FetchEventScheduler fetchEventScheduler;


	@Override
	public GenericResponseWrapper createJob(JobDTO.Job request) {

		String requestPath = request.getRequestPath();
		String baseURL = request.getBaseURL();
		Optional<ConnectionProperties.Host> host = ConnectionProperties.Host.lookup(baseURL);

		if (host.isEmpty()) {
			throw new IllegalArgumentException("Invalid host");
		}

		if (fetchEventScheduler.checkIfJobNameExist(request.getJobName())) {
			log.error("Job with name:" + request.getJobName() + " already exist");
			throw new RuntimeException("Job with name:" + request.getJobName() + " already exist");
		}


		Runnable task = () -> {
			GenericResponseWrapper genericResponseWrapper = restConnector.restGet(host.get(), requestPath, GenericResponseWrapper.class);
			if (genericResponseWrapper.getData() == null) {
				log.error("Failure calling Job:" + request.getJobName() + "with URL:" + request.getBaseURL() + request.getRequestPath());
			}
		};


		CronTrigger cronTrigger = new CronTrigger(
				request.getCronExpression(),
				TimeZone.getTimeZone(TimeZone.getDefault().toZoneId())
		);
		ScheduledFuture<?> schedule = fetchEventScheduler.schedule(task, cronTrigger);
		fetchEventScheduler.addJob(request, schedule);


		return GenericResponseWrapper.builder()
				.msg("Job created successfully")
				.build();
	}
}
