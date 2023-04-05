package com.muchencute.biz.batch.service;

import com.muchencute.biz.batch.service.service.PostLaunchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.muchencute")
@EnableFeignClients
@EnableScheduling
@Slf4j
public class BizBatchServiceApplication {

  public static void main(String[] args) {

    final var context = SpringApplication.run(BizBatchServiceApplication.class, args);
    System.out.println(">>> Datasource instance: " + context.getBean("dataSource"));
    System.out.println(">>> TransactionManager instance: " + context.getBean("transactionManager"));
    postLaunch(context);
  }

  private static void postLaunch(ApplicationContext context) {

    final var postLaunchService = context.getBean(PostLaunchService.class);
    postLaunchService.abortRunningJobs();
    postLaunchService.removeUnusedJobInstances();
  }
}