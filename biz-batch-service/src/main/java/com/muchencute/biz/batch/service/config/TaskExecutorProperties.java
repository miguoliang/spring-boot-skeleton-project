package com.muchencute.biz.batch.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.batch.task-executor")
@Getter
@Setter
public class TaskExecutorProperties {

  private int corePoolSize = 5;

  private int maxPoolSize = 10;

  private int queueCapacity = 100;

}
