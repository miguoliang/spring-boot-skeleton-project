package com.muchencute.biz.keycloak.request;

import com.muchencute.biz.keycloak.validator.NotSuperAdminRoleId;
import com.muchencute.biz.keycloak.validator.NotSuperAdminUsername;
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
  @NotSuperAdminUsername
  private String username;

  private String password;

  private String name;

  private String groupId;

  private String phoneNumber;

  private String picture;

  @NotSuperAdminRoleId
  private String roleId;
}
