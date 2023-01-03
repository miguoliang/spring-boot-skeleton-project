package com.muchencute.biz.keycloak.request;

import com.muchencute.biz.keycloak.validator.NotSuperAdminRole;
import lombok.Data;

@Data
public class RenameRoleRequest {

  @NotSuperAdminRole
  private String newRoleName;
}
