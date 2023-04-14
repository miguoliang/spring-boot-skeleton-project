package com.muchencute.biz.keycloak.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateGroupRequest {

  private String ID;

  private String name;

  private List<String> roles;

}
