package com.muchencute.biz.keycloak.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Setter
@Getter
@Slf4j
public class KeycloakConfig {

  protected AdminCredential admin;

  protected String authServerUrl;

  protected String realm;

  protected String clientId;

  protected Set<String> protectedUsernames = Set.of();

  protected Set<String> protectedRoleNames = Set.of();

  @Bean
  public Keycloak keycloakAdminClient() {

    // Admin 只能在 master 的 admin-cli 上操作
    return Keycloak.getInstance(authServerUrl, "master", admin.username, admin.password,
      "admin-cli");
  }

  @Getter
  @Setter
  public static class AdminCredential {

    private String username;

    private String password;
  }
}
