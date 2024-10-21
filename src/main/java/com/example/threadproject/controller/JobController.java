package com.example.threadproject.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobLauncher jobLauncher;
    private final Job accountJob;

    @Autowired
    public JobController(JobLauncher jobLauncher, Job accountJob) {
        this.jobLauncher = jobLauncher;
        this.accountJob = accountJob;
    }

    @GetMapping("/start-account-job")
    public ResponseEntity<String> startAccountJob(@RequestParam(value = "startAt", required = false) String startAt) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("startAt", startAt != null ? startAt : String.valueOf(System.currentTimeMillis())) // Unique parameter to avoid re-running the same job instance
                    .toJobParameters();

            log.info("Starting accountJob with parameters: {}", jobParameters);

            JobExecution jobExecution = jobLauncher.run(accountJob, jobParameters);
            log.info("JobExecution status: {}", jobExecution.getStatus());

            return new ResponseEntity<>("Job started successfully. JobExecutionId: " + jobExecution.getId(), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Failed to start accountJob", e);
            return new ResponseEntity<>("Job failed to start", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
