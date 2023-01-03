package com.muchencute.biz.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.muchencute")
@EnableFeignClients
public class BizServiceApplication {

  public static void main(String[] args) {

    SpringApplication.run(BizServiceApplication.class, args);
  }
}
