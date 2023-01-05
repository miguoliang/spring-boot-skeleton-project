package com.muchencute.biz.batch.service.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@Slf4j
public class BatchConfig extends DefaultBatchConfiguration {

  @Override
  protected @NonNull TaskExecutor getTaskExecutor() {

    log.info("使用 SimpleAsyncTaskExecutor");
    return new SimpleAsyncTaskExecutor();
  }
}

