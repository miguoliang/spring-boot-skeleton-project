package com.muchencute.biz.keycloak.controller;

import com.muchencute.biz.keycloak.environment.KeycloakTestEnvironment;
import com.muchencute.biz.keycloak.request.NewRoleRequest;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin", authorities = "role:crud")
@TestPropertySource(properties = "keycloak.protected-role-names=super_admin")
@ContextConfiguration(classes = RoleController.class)
class RoleControllerTest extends KeycloakTestEnvironment {

  @Autowired
  Environment environment;

  @Autowired
  KeycloakUserService keycloakUserService;

  @Autowired
  KeycloakRoleService keycloakRoleService;


  @SuppressWarnings("unchecked")
  void initProtectedRoleNames() {

    final var roleNames = environment.getProperty("keycloak.protected-role-names", Set.class);
    assert roleNames != null;

    roleNames.forEach(it -> {
      final var request = new NewRoleRequest();
      request.setName((String) it);
      keycloakRoleService.newRole(request);
    });

    ReflectionTestUtils.setField(NotProtectedUserOrRoleValidator.class, "initialized", false);
  }


  @Test
  @SneakyThrows
  @Transactional("keycloakTransactionManager")
  void can_not_delete_a_protected_role() {

    initProtectedRoleNames();
    mockMvc.perform(delete("/role/super_admin")).andExpect(status().isBadRequest())
      .andDo(document("role/delete/protected"));
  }

  @Test
  void statRole() {
  }

  @Test
  void newRole() {
  }

  @Test
  void updateRole() {
  }

  @Test
  void getRoles() {
  }

  @Test
  void getRole() {
  }

  @Test
  void renameRole() {
  }
}
