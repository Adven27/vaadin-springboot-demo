package com.sberbank.cms.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

//@Component
public class RunScheduler {
    private final JobLauncher jobLauncher;
    private final Job job;

    public RunScheduler(JobLauncher jobLauncher, Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    //@Scheduled(fixedRate = 2000)
    public void run() {
        try {
            JobExecution execution = jobLauncher.run(
                    job,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters()
            );
            System.out.println("Exit status : " + execution.getStatus());
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobRestartException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}