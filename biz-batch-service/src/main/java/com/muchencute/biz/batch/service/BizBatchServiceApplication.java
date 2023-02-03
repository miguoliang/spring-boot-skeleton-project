package com.muchencute.biz.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@SpringBootApplication(scanBasePackages = "com.muchencute")
@EnableFeignClients
@Slf4j
public class BizBatchServiceApplication {

  public static void main(String[] args) {

    final var context = SpringApplication.run(BizBatchServiceApplication.class, args);
    abandonRunningJobs(context);
  }

  private static void abandonRunningJobs(ApplicationContext context) {

    final var jobExplorer = (JobExplorer) context.getBean("jobExplorer");
    final var jobOperator = (JobOperator) context.getBean("jobOperator");
    final var jobRepository = (JobRepository) context.getBean("jobRepository");
    log.info("启动时检查未完成的任务……");
    jobExplorer.getJobNames().forEach(jobName -> {
      log.info("检查任务 {} ...", jobName);
      try {
        jobOperator.getRunningExecutions(jobName).forEach(execution -> {
          log.info("停止中 ... {}", execution);
          Optional.ofNullable(jobExplorer.getJobExecution(execution)).ifPresent(jobExecution -> {
            jobExecution.getStepExecutions().forEach(stepExecution -> {
              log.info("停止 Step ...");
              stepExecution.setStatus(BatchStatus.ABANDONED);
              stepExecution.setExitStatus(ExitStatus.FAILED);
              jobRepository.update(stepExecution);
            });
            jobExecution.setStatus(BatchStatus.ABANDONED);
            jobExecution.setExitStatus(ExitStatus.FAILED);
            jobRepository.update(jobExecution);
            log.info("已停止！");
          });
        });
      } catch (NoSuchJobException ignored) {
      }
    });
  }
}