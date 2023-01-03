package com.muchencute.biz.batch.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BizBatchServiceApplication {

  public static void main(String[] args) {

    SpringApplication.run(BizBatchServiceApplication.class, args);
  }
}