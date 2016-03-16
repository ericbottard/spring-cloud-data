package org.springframework.cloud.dataflow.rest.resource;

import java.util.Date;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.hateoas.ResourceSupport;

/**
 * @author Glenn Renfro
 */
public class StepExecutionResource extends ResourceSupport{

	private long jobExecutionId;

	private long stepExecutionId;

	private String stepName;

	private Date startTime;

	private Date endTime;

	private BatchStatus status;

	private Date lastUpdated;

	private int readCount;

	private int writeCount;

	private int filterCount;

	private int readSkipCount;

	private int writeSkipCount;

	private int processSkipCount;

	private int commitCount;

	private int rollbackCount;

	private ExitStatus exitStatus;

	private String exitDescription;

	/**
	 *
	 * @param jobExecutionId
	 * @param stepExecutionId
	 */
	public StepExecutionResource(long jobExecutionId, long stepExecutionId,
			String stepName, Date startTime, Date endTime, BatchStatus status,
			Date lastUpdated, int readCount, int writeCount, int filterCount,
			int readSkipCount, int writeSkipCount, int processSkipCount, int commitCount,
			int rollbackCount, ExitStatus exitStatus, String exitDescription) {
		this.stepExecutionId = stepExecutionId;
		this.jobExecutionId = jobExecutionId;
		this.stepName = stepName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.lastUpdated = lastUpdated;
		this.readCount = readCount;
		this.writeCount = writeCount;
		this.filterCount = filterCount;
		this.readSkipCount = readSkipCount;
		this.writeSkipCount = writeSkipCount;
		this. processSkipCount = processSkipCount;
		this. commitCount = commitCount;
		this.rollbackCount = rollbackCount;
		this.exitStatus = exitStatus;
		this. exitDescription = exitDescription;
	}

	public StepExecutionResource() {
		this.stepExecutionId = 0l;
		this.jobExecutionId = 0l;
		stepName = null;
	}

	/**
	 * @return The stepExecutionId
	 */
	public long getStepExecutionId() {
		return stepExecutionId;
	}

	public String getStepName() {
		return stepName;
	}

	public long getJobExecutionId(){
		return jobExecutionId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public BatchStatus getStatus() {
		return status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public int getReadCount() {
		return readCount;
	}

	public int getWriteCount() {
		return writeCount;
	}

	public int getFilterCount() {
		return filterCount;
	}

	public int getReadSkipCount() {
		return readSkipCount;
	}

	public int getWriteSkipCount() {
		return writeSkipCount;
	}

	public int getProcessSkipCount() {
		return processSkipCount;
	}

	public int getCommitCount() {
		return commitCount;
	}

	public int getRollbackCount() {
		return rollbackCount;
	}

	public ExitStatus getExitStatus() {
		return exitStatus;
	}

	public String getExitDescription() {
		return exitDescription;
	}
}
