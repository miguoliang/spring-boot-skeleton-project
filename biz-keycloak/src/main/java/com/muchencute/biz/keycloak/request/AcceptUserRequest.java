package com.muchencute.biz.keycloak.request;

import lombok.Data;

@Data
public class AcceptUserRequest {

  private String roleName;
  private String memo;
}
