package com.muchencute.biz.service;

import com.muchencute.biz.batch.service.client.BizBatchServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.muchencute")
@EnableFeignClients(clients = BizBatchServiceClient.class)
public class BizServiceApplication {

  public static void main(String[] args) {

    SpringApplication.run(BizServiceApplication.class, args);
  }
}
