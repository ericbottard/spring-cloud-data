/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.rest.resource;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.hateoas.ResourceSupport;

/**
 * A HATEOAS representation of a JobExecution.
 *
 * @author Glenn Renfro
 */
public class JobExecutionResource extends ResourceSupport {

	/**
	 * The unique id  associated with the task execution.
	 */
	private long taskExecutionId;

	/**
	 * The unique id  associated with the job execution.
	 */
	private long jobExecutionId;


	/**
	 * The recorded batch status for the Job execution.
	 */
	private BatchStatus batchStatus;

	/**
	 * Time of when the Job was started.
	 */
	private Date startTime;

	/**
	 * Timestamp of when the Job was completed/terminated.
	 */
	private Date endTime;

	/**
	 * ExitStatus returned from the Job or stacktrace.parameters.
	 */
	private ExitStatus exitStatus;

	/**
	 * The parameters that were used for this job execution.
	 */
	private JobParameters parameters;

	/**
	 * The name associated with the job.
	 */
	private String jobName;

	public JobExecutionResource(long taskExecutionId, long jobExecutionId,
			BatchStatus batchStatus, Date startTime, Date endTime, ExitStatus exitStatus,
			JobParameters parameters, String jobName) {

		this.taskExecutionId = taskExecutionId;
		this.jobExecutionId = jobExecutionId;
		this.batchStatus = batchStatus;
		this.startTime = startTime;
		this.endTime = endTime;
		this.exitStatus = exitStatus;
		this.parameters = parameters;
		this.jobName = jobName;
	}

	public JobParameters getParameters() {
		return parameters;
	}

	public BatchStatus getBatchStatus() {
		return batchStatus;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public ExitStatus getExitStatus() {
		return exitStatus;
	}

	public long getTaskExecutionId() {
		return taskExecutionId;
	}

	public long getJobExecutionId() {
		return jobExecutionId;
	}

	public String getJobName() {
		return jobName;
	}
}
