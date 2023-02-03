package com.muchencute.biz.batch.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Slf4j
@EnableBatchProcessing
public class BatchConfig {

  @Bean
  public TaskExecutor taskExecutor() {

    log.info("使用 SimpleAsyncTaskExecutor");
    return new SimpleAsyncTaskExecutor();
  }
}

