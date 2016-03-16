package org.springframework.cloud.dataflow.server.repository;


import org.springframework.batch.core.JobExecution;

/**
 * @author Glenn Renfro
 */
public class TaskJobExecution {
	private long taskId;
	private JobExecution jobExecution;
	public TaskJobExecution(long taskId, JobExecution jobExecution) {
		this.taskId = taskId;
		this.jobExecution = jobExecution;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public JobExecution getJobExecution() {
		return jobExecution;
	}

	public void setJobExecution(JobExecution jobExecution) {
		this.jobExecution = jobExecution;
	}

}
