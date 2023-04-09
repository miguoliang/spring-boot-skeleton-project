package com.muchencute.biz.keycloak.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {

  private String name;

  private String groupId;

  private String phoneNumber;

  private String roleId;

  private String picture;
}
