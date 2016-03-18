package org.springframework.cloud.dataflow.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.admin.service.JobService;
import org.springframework.batch.admin.service.NoSuchStepExecutionException;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.dataflow.rest.resource.StepExecutionResource;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Glenn Renfro
 */
@RestController
@RequestMapping("/jobs/executions/{jobExecutionId}/steps")
@ExposesResourceFor(StepExecutionResource.class)
public class JobStepExecutionController {

	private final JobService jobService;

	private final Assembler stepAssembler = new Assembler();

	/**
	 * Creates a {@code JobStepExecutionsController} that retrieves Job Step Execution
	 * information from a the {@link JobService}
	 *
	 * @param jobService the service this controller will use for retrieving
	 *  job execution information.
	 */
	@Autowired
	public JobStepExecutionController(JobService jobService) {
		Assert.notNull(jobService, "repository must not be null");
		this.jobService = jobService;
	}

	/**
	 * List all step executions.
	 *
	 * @param id the {@link JobExecution}.
	 * @return Collection of {@link StepExecutionResource} for the given jobExecutionId
	 */
	@RequestMapping(value = { "" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public List<StepExecutionResource> stepExecutions(
			@PathVariable("jobExecutionId") long id) throws NoSuchJobExecutionException {
		List<StepExecution> result = new ArrayList<>(jobService.getStepExecutions(id));
		return stepAssembler.toResources(result);
	}

	/**
	 * Retrieve a specific {@link StepExecutionResource}.
	 *
	 * @param id the {@link JobExecution} id.
	 * @param stepId the {@link StepExecution} id.
	 * @return Collection of {@link StepExecutionResource} for the given jobExecutionId
	 */
	@RequestMapping(value = { "/{stepExecutionId}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public StepExecutionResource getStepExecution(
			@PathVariable("jobExecutionId") long id,
			@PathVariable("stepExecutionId") long stepId) throws
			NoSuchStepExecutionException, NoSuchJobExecutionException {
		return stepAssembler.toResource(jobService.getStepExecution(id, stepId));
	}

	/**
	 * {@link org.springframework.hateoas.ResourceAssembler} implementation
	 * that converts {@link StepExecution}s to {@link StepExecutionResource}s.
	 */
	private static class Assembler extends ResourceAssemblerSupport<StepExecution, StepExecutionResource> {

		public Assembler() {
			super(JobStepExecutionController.class, StepExecutionResource.class);
		}

		@Override
		public StepExecutionResource toResource(StepExecution stepExecution) {
			return createResourceWithId(stepExecution.getId(), stepExecution, stepExecution.getJobExecution().getId());
		}

		@Override
		public StepExecutionResource instantiateResource(StepExecution stepExecution) {
			return new StepExecutionResource(
					stepExecution.getJobExecutionId(),stepExecution.getId(),
					stepExecution.getStepName(), stepExecution.getStartTime(),
					stepExecution.getEndTime(), stepExecution.getStatus(),
					stepExecution.getLastUpdated(), stepExecution.getReadCount(),
					stepExecution.getWriteCount(), stepExecution.getFilterCount(),
					stepExecution.getReadSkipCount(), stepExecution.getWriteSkipCount(),
					stepExecution.getProcessSkipCount(), stepExecution.getCommitCount(),
					stepExecution.getRollbackCount(), stepExecution.getExitStatus(),
					stepExecution.getExitStatus().getExitDescription());
		}
	}
}
