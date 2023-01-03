package com.muchencute.biz.batch.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "biz-batch-service", path = "/api/v1")
public interface BizBatchClient {

}
