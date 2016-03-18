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

package org.springframework.cloud.dataflow.server.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.cloud.dataflow.server.configuration.JobDependencies;
import org.springframework.cloud.dataflow.server.configuration.TestDependencies;
import org.springframework.cloud.task.batch.listener.TaskBatchDao;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.cloud.task.repository.dao.TaskExecutionDao;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Glenn Renfro
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedDataSourceConfiguration.class,
		TestDependencies.class, JobDependencies.class,
		PropertyPlaceholderAutoConfiguration.class, BatchProperties.class })
@WebAppConfiguration
@DirtiesContext
public class JobStepExecutionControllerTests {

	private final static String BASE_JOB_NAME = "myJob";

	private final static String JOB_NAME_ORIG = BASE_JOB_NAME + "_ORIG";

	private final static String JOB_NAME_FOO = BASE_JOB_NAME + "_FOO";

	private final static String JOB_NAME_FOOBAR = BASE_JOB_NAME + "_FOOBAR";

	private final static String BASE_STEP_NAME = "myStep";

	private final static String STEP_NAME_ORIG = BASE_STEP_NAME + "_ORIG";

	private final static String STEP_NAME_FOO = BASE_STEP_NAME + "_FOO";

	private final static String STEP_NAME_FOOBAR = BASE_STEP_NAME + "_FOOBAR";


	private static boolean initialized = false;

	@Autowired
	private TaskExecutionDao dao;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private TaskBatchDao taskBatchDao;

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Before
	public void setupMockMVC() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).defaultRequest(
				get("/").accept(MediaType.APPLICATION_JSON)).build();
		if (!initialized) {

			JobInstance instance = jobRepository.createJobInstance(JOB_NAME_ORIG, new JobParameters());
			JobExecution jobExecution = jobRepository.createJobExecution(
					instance, new JobParameters(), null);
			jobRepository.add(createStepExecution(jobExecution, STEP_NAME_ORIG));
			TaskExecution taskExecution = dao.createTaskExecution(
					JOB_NAME_ORIG, new Date(), new ArrayList<String>());
			taskBatchDao.saveRelationship(taskExecution, jobExecution);

			instance = jobRepository.createJobInstance(JOB_NAME_FOO, new JobParameters());
			jobExecution = jobRepository.createJobExecution(
					instance, new JobParameters(), null);
			jobRepository.add(createStepExecution(jobExecution, STEP_NAME_ORIG));
			jobRepository.add(createStepExecution(jobExecution, STEP_NAME_FOO));
			taskExecution = dao.createTaskExecution(
					JOB_NAME_FOO, new Date(), new ArrayList<String>());
			taskBatchDao.saveRelationship(taskExecution, jobExecution);

			instance = jobRepository.createJobInstance(JOB_NAME_FOOBAR, new JobParameters());
			jobExecution = jobRepository.createJobExecution(
					instance, new JobParameters(), null);
			jobRepository.add(createStepExecution(jobExecution, STEP_NAME_ORIG));
			jobRepository.add(createStepExecution(jobExecution, STEP_NAME_FOO));
			jobRepository.add(createStepExecution(jobExecution, STEP_NAME_FOOBAR));
			taskExecution = dao.createTaskExecution(
					JOB_NAME_FOOBAR, new Date(), new ArrayList<String>());
			taskBatchDao.saveRelationship(taskExecution, jobExecution);

			initialized = true;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testJobStepExecutionControllerConstructorMissingRepository() {
		new JobStepExecutionController(null);
	}

	@Test
	public void testGetExecutionNotFound() throws Exception {
		mockMvc.perform(
				get("/jobs/executions/steps/1342434234").accept(MediaType.APPLICATION_JSON)
		).andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void testSingleGetStepExecution() throws Exception {
		mockMvc.perform(
				get("/jobs/executions/steps/step?jobExecutionId=1&stepExecutionId=1").accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk()).andExpect(content().json("{jobExecutionId: " +
				1 + "}"));
	}

	@Test
	public void testGetMultipleStepExecutions() throws Exception {
		mockMvc.perform(
				get("/jobs/executions/steps/3").accept(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].stepExecutionId", is(4)))
				.andExpect(jsonPath("$[1].stepExecutionId", is(5)))
				.andExpect(jsonPath("$[2].stepExecutionId", is(6)));
	}

	private StepExecution createStepExecution(JobExecution jobExecution, String stepName) {
		StepExecution stepExecution = new StepExecution(stepName, jobExecution, 1L);
		stepExecution.setId(null);
		return stepExecution;
	}
}
