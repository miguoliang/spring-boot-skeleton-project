package com.muchencute.biz.service.integration.controller;

import com.muchencute.test.environment.KeycloakTestEnvironment;
import com.muchencute.test.environment.mock.MockUser;
import com.muchencute.test.environment.service.KeycloakAccessTokenService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WithMockUser(username = "admin", authorities = {
  "user:crud",
  "user:check"
})
class UserControllerTest extends KeycloakTestEnvironment {

  @Autowired
  private KeycloakAccessTokenService keycloakAccessTokenService;

  @Test
  @SneakyThrows
  @MockUser(username = "user_1", password = "user_1")
  @MockUser(username = "user_2", password = "user_2")
  @MockUser(username = "user_3", password = "user_3")
  @MockUser(username = "user_4", password = "user_4")
  @MockUser(username = "user_5", password = "user_5")
  void testOnlineUserNum() {

    keycloakAccessTokenService.getBearer(keycloak, "user_1", "user_1");
    keycloakAccessTokenService.getBearer(keycloak, "user_2", "user_2");
    keycloakAccessTokenService.getBearer(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.getBearer(keycloak, "user_4", "user_4");

    mockMvc.perform(get("/user/onlineNum"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.count", equalTo(4)));
  }

  @Test
  @SneakyThrows
  @MockUser(username = "user_1", password = "user_1")
  @MockUser(username = "user_2", password = "user_2")
  @MockUser(username = "user_3", password = "user_3")
  @MockUser(username = "user_4", password = "user_4")
  @MockUser(username = "user_5", password = "user_5")
  void testOfflineUserNum() {

    keycloakAccessTokenService.getBearer(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.getBearer(keycloak, "user_4", "user_4");

    mockMvc.perform(get("/user/offlineNum"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.count", equalTo(4)));
  }
}