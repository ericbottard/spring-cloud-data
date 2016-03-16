package org.springframework.cloud.dataflow.server.controller;

import java.util.ArrayList;

import org.springframework.batch.admin.service.JobService;
import org.springframework.batch.admin.service.NoSuchStepExecutionException;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.dataflow.rest.resource.StepExecutionResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
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

	private JobService jobService;

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
	public PagedResources<StepExecutionResource> list(
			@PathVariable("jobExecutionId") long id, Pageable pageable,
			PagedResourcesAssembler<StepExecution> assembler) {
		Page page = null;
		try{
			page = new PageImpl<>(new ArrayList<StepExecution>(jobService.getStepExecutions(id)), pageable,
					jobService.getStepExecutions(id).size());
		}catch (NoSuchJobExecutionException e){
			page = new PageImpl<>(new ArrayList<StepExecutionResource>());
		}
		return assembler.toResource(page, stepAssembler);
	}

	/**
	 * List all step executions.
	 *
	 * @param id the {@link JobExecution} id.
	 * @param stepId the {@ling StepExecution} id.
	 * @return a {@link StepExecutionResource} for the given jobExecutionId and
	 * stepExecutionId
	 */
	@RequestMapping(value = { "/{stepId}" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public StepExecutionResource view(
			@PathVariable("jobExecutionId") long id, @PathVariable("stepId") long stepId,Pageable pageable,
			PagedResourcesAssembler<StepExecution> assembler) throws NoSuchStepExecutionException, NoSuchJobExecutionException {
		Page page = null;
		StepExecution stepExecution = jobService.getStepExecution(id,stepId);
		return stepAssembler.toResource(stepExecution);
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
			return createResourceWithId(stepExecution.getJobExecution().getId(), stepExecution);
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
