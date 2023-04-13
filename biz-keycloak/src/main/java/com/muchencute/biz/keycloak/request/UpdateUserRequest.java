package com.muchencute.biz.keycloak.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

  private String name;

  private String groupId;

  private String phoneNumber;

  private String roleId;

  private String picture;
}
