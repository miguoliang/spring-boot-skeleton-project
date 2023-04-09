package com.muchencute.biz.keycloak.controller;

import com.muchencute.biz.keycloak.environment.KeycloakTestEnvironment;
import com.muchencute.biz.keycloak.environment.mock.MockUser;
import com.muchencute.biz.keycloak.environment.service.KeycloakAccessTokenService;
import com.muchencute.biz.keycloak.repository.UserEntityRepository;
import com.muchencute.biz.keycloak.request.NewUserRequest;
import com.muchencute.biz.keycloak.service.KeycloakRoleService;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import com.muchencute.biz.keycloak.validator.NotProtectedUserOrRoleValidator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin", authorities = {
  "user:crud",
  "user:check"
})
@TestPropertySource(properties = "keycloak.protected-usernames=super_admin")
@ContextConfiguration(classes = UserController.class)
class UserControllerTest extends KeycloakTestEnvironment {

  @Autowired
  Environment environment;

  @Autowired
  KeycloakUserService keycloakUserService;

  @Autowired
  KeycloakRoleService keycloakRoleService;

  @Autowired
  KeycloakAccessTokenService keycloakAccessTokenService;

  @Autowired
  UserEntityRepository userEntityRepository;


  @SuppressWarnings("unchecked")
  void initProtectedUsers() {

    final var usernames = environment.getProperty("keycloak.protected-usernames", Set.class);
    assert usernames != null;

    usernames.forEach(it -> {
      final var request = new NewUserRequest();
      request.setUsername((String) it);
      request.setPassword((String) it);
      keycloakUserService.newUser(request);
    });

    ReflectionTestUtils.setField(NotProtectedUserOrRoleValidator.class, "initialized", false);
  }

  @Test
  @SneakyThrows
  @MockUser(username = "user_1", password = "user_1")
  @MockUser(username = "user_2", password = "user_2")
  @MockUser(username = "user_3", password = "user_3")
  @MockUser(username = "user_4", password = "user_4")
  @MockUser(username = "user_5", password = "user_5")
  @Transactional("keycloakTransactionManager")
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
  @Transactional("keycloakTransactionManager")
  void calc_offline_num() {

    keycloakAccessTokenService.login(keycloak, "user_3", "user_3");
    keycloakAccessTokenService.login(keycloak, "user_4", "user_4");

    mockMvc.perform(get("/user/offlineNum"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.count", equalTo(3)));
  }

  @Test
  @SneakyThrows
  @Transactional("keycloakTransactionManager")
  void can_not_delete_protected_user() {

    initProtectedUsers();
    final var idOfRealm = keycloakService.getIdOfRealm();
    final var user = userEntityRepository.findByUsernameAndRealmId("super_admin", idOfRealm).orElseThrow();
    mockMvc.perform(delete("/user/{id}", user.getId()))
      .andExpect(status().isBadRequest());
  }
}