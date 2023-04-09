package com.muchencute.biz.keycloak.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequest {

  @NotBlank
  private String username;

  private String password;

  private String name;

  private String groupId;

  private String phoneNumber;

  private String picture;

  private String roleId;
}
