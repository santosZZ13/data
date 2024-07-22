package org.data.job.service;

import lombok.AllArgsConstructor;
import org.data.common.model.GenericResponseWrapper;
import org.data.config.FetchEventScheduler;
import org.data.job.dto.JobDTO;
import org.data.job.exception.JobAlreadyExistException;
import org.data.job.exception.NotFoundServiceException;
import org.data.properties.ConnectionProperties;
import org.data.util.RestConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
@AllArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

	private static final Logger log = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	private final RestConnector restConnector;
	private final ConnectionProperties connectionProperties;
	private final FetchEventScheduler fetchEventScheduler;
	private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@Override
	public GenericResponseWrapper createJob(JobDTO.Request request) {
		JobDTO.Job job = request.getJob();
		String requestPath = job.getRequestPath();
		String service = job.getService();
		Optional<ConnectionProperties.Host> host = ConnectionProperties.Host.lookup(service);

		if (host.isEmpty()) {
			throw new NotFoundServiceException("Service not found", "", "");
		}

		if (fetchEventScheduler.checkIfJobNameExist(job.getJobName())) {
			log.info("Job with name:{} already exist", job.getJobName());
			throw new JobAlreadyExistException("Job with name:" + job.getJobName() + " already exist", "", "");
		}


		Runnable task = () -> {
			log.info("----------------------------------------------------------------------------");
			log.info("#CreateJob Starting Job: {} with URL: {}{}", job.getJobName(), job.getService(), job.getRequestPath());

			GenericResponseWrapper genericResponseWrapper = restConnector.restGet(host.get(), requestPath, GenericResponseWrapper.class);
			if (genericResponseWrapper == null) {
				log.error("Failure calling Job: {} with URL: {}{}", job.getJobName(), job.getService(), job.getRequestPath());
			}

			log.info("Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
			log.info("Current Thread : {}", Thread.currentThread().getName());
			log.info("Job: {} with URL: {}{} completed", job.getJobName(), job.getService(), job.getRequestPath());
			log.info("----------------------------------------------------------------------------");
		};


		CronTrigger cronTrigger = new CronTrigger(
				job.getCronExpression(),
				TimeZone.getTimeZone(TimeZone.getDefault().toZoneId())
		);

		ScheduledFuture<?> schedule = fetchEventScheduler.schedule(task, cronTrigger);
		fetchEventScheduler.addJob(job, schedule);


		return GenericResponseWrapper.builder()
				.msg("Job created successfully")
				.build();
	}


	@Override
	public GenericResponseWrapper getJobs() {
		Map<JobDTO.Job, ScheduledFuture<?>> tasks = fetchEventScheduler.getScheduledTasks();
		List<JobDTO.Job> jobs = new ArrayList<>();
		tasks.forEach((job, scheduledFuture) -> {
			jobs.add(job);
		});

		return GenericResponseWrapper.builder()
				.data(jobs)
				.build();
	}

	@Override
	public GenericResponseWrapper deleteJob(String jobName) {
		fetchEventScheduler.removeJob(jobName);
		return GenericResponseWrapper.builder()
				.msg("Job deleted successfully")
				.build();
	}
}
