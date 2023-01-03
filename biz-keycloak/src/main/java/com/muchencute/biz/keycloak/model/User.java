package com.muchencute.biz.keycloak.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

  private String id;

  private String username;

  @NotBlank
  private String name;

  private String picture;

  private String phoneNumber;

  private Group group;

  private Role role;

  private Boolean enabled;

  private String commonIp;

  private String lastLoginIp;

  private Long lastLoginTime;

  private Long createdAt;
}
