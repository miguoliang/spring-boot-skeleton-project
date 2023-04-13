package com.muchencute.biz.keycloak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchencute.biz.keycloak.environment.KeycloakTestEnvironment;
import com.muchencute.biz.keycloak.environment.mock.MockUser;
import com.muchencute.biz.keycloak.environment.service.KeycloakAccessTokenService;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.request.NewUserRequest;
import com.muchencute.biz.keycloak.request.UpdateUserRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.BeforeTransaction;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin", authorities = {
  "user:crud",
  "user:check"
})
@TestPropertySource(properties = "keycloak.protected-usernames=super_admin")
@ContextConfiguration(classes = UserController.class)
@Slf4j
class UserControllerTest extends KeycloakTestEnvironment {

  final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Environment environment;

  @Autowired
  KeycloakAccessTokenService keycloakAccessTokenService;

  @Autowired
  UserEntityRepository userEntityRepository;

  @BeforeTransaction
  void beforeTransaction() {

    userEntityRepository.flush();
  }

  @Test
  @SneakyThrows
  @MockUser(username = "user_1", password = "user_1")
  @MockUser(username = "user_2", password = "user_2")
  @MockUser(username = "user_3", password = "user_3")
  @MockUser(username = "user_4", password = "user_4")
  @MockUser(username = "user_5", password = "user_5")
  void calc_online_num() {

    keycloakAccessTokenService.login(keycloak, "user_1", "user_1");
    keycloakAccessTokenService.login(keycloak, "user_2", "user_2");
    keycloakAccessTokenService.login(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.login(keycloak, "user_4", "user_4");

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
  void calc_offline_num() {

    keycloakAccessTokenService.login(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.login(keycloak, "user_4", "user_4");

    mockMvc.perform(get("/user/offlineNum"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.count", equalTo(3)));
  }

  @Test
  @SneakyThrows
  void user_new_ok() {

    final var user = new NewUserRequest();
    user.setUsername("user_1");
    user.setPassword("user_1");

    mockMvc.perform(post("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(user)))
      .andExpect(status().isOk())
      .andDo(document("user/new/ok"));
  }

  @Test
  @MockUser(username = "user_1")
  @SneakyThrows
  void user_get_single_ok() {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var user = userEntityRepository.findByUsernameAndRealmId("user_1", idOfRealm).orElseThrow();
    mockMvc.perform(get("/user/{id}", user.getId()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", equalTo(user.getId())))
      .andExpect(jsonPath("$.username", equalTo("user_1")))
      .andDo(document("user/get/single/ok"));
  }

  @Test
  @MockUser(username = "user_1")
  @SneakyThrows
  void user_get_single_not_exits() {

    mockMvc.perform(get("/user/{id}", "not-exists"))
      .andExpect(status().isNotFound())
      .andDo(document("user/get/single/not-exists"));
  }

  @Test
  @MockUser(username = "user_1")
  @MockUser(username = "user_2")
  @SneakyThrows
  void user_get_list_ok() {

    mockMvc.perform(get("/user"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.content.length()", equalTo(2)))
      .andExpect(jsonPath("$.content.[*].username", hasItems("user_1", "user_2")))
      .andDo(document("user/get/list/ok"));
  }

  @Test
  @MockUser(username = "user_1")
  @SneakyThrows
  void user_update_ok() {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var user = userEntityRepository.findByUsernameAndRealmId("user_1", idOfRealm).orElseThrow();
    final var updateRequest = new UpdateUserRequest();
    updateRequest.setName("user_1_updated");
    mockMvc.perform(put("/user/{id}", user.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", equalTo(user.getId())))
      .andExpect(jsonPath("$.name", equalTo("user_1_updated")))
      .andDo(document("user/update/ok"));
  }

  @Test
  @MockUser(username = "super_admin")
  @SneakyThrows
  void user_update_protected_should_fail() {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var user = userEntityRepository.findByUsernameAndRealmId("super_admin", idOfRealm).orElseThrow();
    final var updateRequest = new UpdateUserRequest();
    updateRequest.setName("user_1_updated");
    mockMvc.perform(put("/user/{id}", user.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
      .andExpect(status().isBadRequest())
      .andDo(document("user/update/protected"));
  }

  @Test
  @MockUser(username = "user_1")
  @SneakyThrows
  void user_update_not_exists() {

    final var updateRequest = new UpdateUserRequest();
    updateRequest.setName("user_1_updated");
    mockMvc.perform(put("/user/{id}", "not-exists")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)))
      .andExpect(status().isNotFound())
      .andDo(document("user/update/not-exists"));
  }

  @Test
  @MockUser(username = "super_admin")
  @SneakyThrows
  void user_delete_protected_should_fail() {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var user = userEntityRepository.findByUsernameAndRealmId("super_admin", idOfRealm).orElseThrow();
    mockMvc.perform(delete("/user/{id}", user.getId()))
      .andExpect(status().isBadRequest())
      .andDo(document("user/delete/protected-fail"));
  }

  @Test
  @MockUser(username = "user_1", password = "user_1")
  @SneakyThrows
  void user_delete_ok() {

    final var idOfRealm = keycloakService.getIdOfRealm();
    final var user = userEntityRepository.findByUsernameAndRealmId("user_1", idOfRealm).orElseThrow();
    mockMvc.perform(delete("/user/{id}", user.getId()))
      .andExpect(status().isOk())
      .andDo(document("user/delete/ok"));
  }

  @Test
  @SneakyThrows
  void user_delete_not_exists() {

    mockMvc.perform(delete("/user/{id}", "not-exists"))
      .andExpect(status().isNotFound())
      .andDo(document("user/delete/not-exists"));
  }

  @Test
  @SneakyThrows
  void user_delete_self_should_fail() {

    // Test Profile 下 JwtHelper.getUserId 会返回 username 本身。
    mockMvc.perform(delete("/user/admin"))
      .andExpect(status().isBadRequest())
      .andDo(document("user/delete/self-fail"));
  }
}