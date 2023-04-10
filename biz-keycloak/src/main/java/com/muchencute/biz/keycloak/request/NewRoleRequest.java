package com.muchencute.biz.keycloak.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewRoleRequest {

  private String name;

  private Set<String> scopes = Set.of();
}
