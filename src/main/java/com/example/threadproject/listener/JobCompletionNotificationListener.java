package com.example.threadproject.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JobLauncher jobLauncher;
    private final Job customerJob;

    @Autowired
    public JobCompletionNotificationListener(JobLauncher jobLauncher, Job customerJob) {
        this.jobLauncher = jobLauncher;
        this.customerJob = customerJob;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting Job: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job completed successfully: {}", jobExecution.getJobInstance().getJobName());

            // Start the next job (customerJob) after accountJob completes
            if ("accountJob".equals(jobExecution.getJobInstance().getJobName())) {

                // Start customerJob
                try {
                    log.info("Starting customerJob as the accountJob has completed.");
                    jobLauncher.run(customerJob, jobExecution.getJobParameters());
                } catch (Exception e) {
                    log.error("Failed to start customerJob after accountJob completion.", e);
                }
            }
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job failed with status: {}", jobExecution.getStatus());
        }
    }
}
