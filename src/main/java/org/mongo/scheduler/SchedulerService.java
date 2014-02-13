package org.mongo.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduler for handling batch jobs. This class is not used at the prod application
 */
public class SchedulerService {

	protected static Logger logger = LoggerFactory
			.getLogger(SchedulerService.class);


	/**
	 * You can opt for cron expression or fixedRate or fixedDelay
	 * <p>
	 * 
	 * 
	 * See Spring Framework 3 Reference: Chapter 25.5 Annotation Support for
	 * Scheduling and Asynchronous Execution
	 */
	@Scheduled(fixedRate=500000)
	public void work() {
		logger.info("invoking scheduler");
	}

}