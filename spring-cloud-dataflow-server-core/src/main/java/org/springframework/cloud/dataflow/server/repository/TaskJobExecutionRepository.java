package org.springframework.cloud.dataflow.server.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.batch.admin.service.JobService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.repository.TaskExplorer;
import org.springframework.data.domain.Pageable;

/**
 * @author Glenn Renfro.
 */
public class TaskJobExecutionRepository {

	@Autowired
	private TaskExplorer taskExplorer;

	@Autowired
	private JobService jobService;

	/**
	 * Retrieves Pageable list of {@link JobExecution}s from the JobRepository and matches
	 * the data with a task id.
	 * @param pageable enumerates the data to be returned.
	 * @return List containing {@link TaskJobExecution}s.
	 */
	public List<TaskJobExecution> listJobExecutions(Pageable pageable){
		return getTaskJobExecutionsForList(
				jobService.listJobExecutions(pageable.getOffset(), pageable.getPageSize()));
	}

	/**
	 * Retrieves Pageable list of {@link JobExecution} from the JobRepository with a
	 * specific jobName and matches the data with a task id.
	 * @param pageable enumerates the data to be returned.
	 * @param jobName the name of the job for which to search.
	 * @return List containing {@link TaskJobExecution}s.
	 */
	public List<TaskJobExecution> listJobExecutionsForJob (Pageable pageable,
			String jobName) throws NoSuchJobException{
		return getTaskJobExecutionsForList(
				jobService.listJobExecutionsForJob(jobName,pageable.getOffset(),
						pageable.getPageSize()));
	}

	/**
	 * Retrieves a JobExecution from the JobRepository and matches it with a task id.
	 * @param id the id of the {@link JobExecution}
	 * @return the {@link TaskJobExecution}s associated with the id.
	 */
	public TaskJobExecution getJobExecution(long id) throws NoSuchJobExecutionException{
		JobExecution jobExecution = jobService.getJobExecution(id);
		jobExecution.setStartTime(new Date());
		return getTaskJobExecution(jobExecution);
	}

	/**
	 * Retrieves the total number of the job executions.
	 */
	public int countJobExecutions(){
		return jobService.countJobExecutions();
	}

	/**
	 * Retrieves the total number {@link JobExecution} that match a specific job name.
	 * @param jobName the job name to search.
	 * @return the number of {@link JobExecution}s that match the job name.
	 * @throws NoSuchJobException
	 */
	public int countJobExecutionsForJob(String jobName) throws NoSuchJobException{
		return jobService.countJobExecutionsForJob(jobName);
	}

	private List<TaskJobExecution> getTaskJobExecutionsForList(Collection<JobExecution> jobExecutions){
		List<TaskJobExecution> taskJobExecutions = new ArrayList<>();
		for(JobExecution jobExecution : jobExecutions){
			taskJobExecutions.add(getTaskJobExecution(jobExecution));
		}
		return taskJobExecutions;
	}

	private TaskJobExecution getTaskJobExecution(JobExecution jobExecution){
		return new TaskJobExecution(
				taskExplorer.getTaskExecutionIdByJobExecutionId(jobExecution.getJobId()),
				jobExecution);
	}
}
