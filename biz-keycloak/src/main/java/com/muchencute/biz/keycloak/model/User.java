package com.muchencute.biz.keycloak.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private String id;

  private String username;

  @NotBlank
  private String name;

  private String picture;

  private String phoneNumber;

  private Set<String> groups;

  private Set<String> roles;

  private Boolean enabled;

  private String commonIp;

  private String lastLoginIp;

  private Long lastLoginTime;

  private Long createdAt;
}
