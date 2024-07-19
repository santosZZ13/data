package org.data.config;

import lombok.Getter;
import org.data.job.dto.JobDTO;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Getter
public class FetchEventScheduler extends ThreadPoolTaskScheduler {
	private static final int POOL_SIZE = 10;
	private final static String THREAD_NAME_PREFIX = "FetchEvent-";
	private final Map<JobDTO.Job, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();

	public FetchEventScheduler() {
		super();
		this.setPoolSize(POOL_SIZE);
		this.setThreadNamePrefix(THREAD_NAME_PREFIX);
		this.initialize();
	}

	public Boolean checkIfJobNameExist(String jobName) {
		return this.scheduledTasks
				.keySet()
				.stream()
				.anyMatch(
						job -> job.getJobName().equals(jobName)
				);
	}


	public void addJob(JobDTO.Job job, ScheduledFuture<?> schedule) {
		this.scheduledTasks.put(job, schedule);
	}

	public void removeJob(String jobName) {
		this.scheduledTasks
				.keySet()
				.stream()
				.filter(
						job -> job.getJobName().equals(jobName)
				)
				.findFirst()
				.ifPresent(
						job -> this.scheduledTasks.get(job).cancel(true)
				);
	}
}
