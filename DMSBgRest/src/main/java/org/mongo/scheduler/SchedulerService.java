package org.mongo.scheduler;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

/**
 * Scheduler for handling jobs
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
	@Scheduled(fixedRate=1800000)
	public void work() {
		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();
		
		// Set the Accept header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
		requestHeaders.add("Accept-Encoding", "gzip,deflate,sdch");
		requestHeaders.add("Accept-Language", "en-US,en;q=0.8");
		HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);
		
		// Make the HTTP GET request, marshaling the response from JSON to an array of Events
		ResponseEntity<String> responseEntity = restTemplate.exchange("http://dms-bg.herokuapp.com/campaign.json?type=1&page=1", HttpMethod.GET, requestEntity, String.class);
				
		System.out.println(responseEntity.getBody());
	}

}