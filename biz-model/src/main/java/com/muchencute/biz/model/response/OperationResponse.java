package com.muchencute.biz.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class OperationResponse {

  private long id;

  private String status;

  private String exitCode;

  private String name;

  private Long startTime;

  private Long endTime;

  private Map<String, Object> ctx = new HashMap<>();

  private Map<String, Object> parameters = new HashMap<>();
}
