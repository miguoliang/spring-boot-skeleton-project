package com.muchencute.biz.keycloak.request;

import java.util.List;

import com.muchencute.biz.keycloak.validator.NotSuperAdminRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewRoleRequest {

  @NotSuperAdminRole
  private String name;

  private List<String> scopes;
}
