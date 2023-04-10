package com.muchencute.biz.keycloak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muchencute.biz.keycloak.environment.KeycloakTestEnvironment;
import com.muchencute.biz.keycloak.environment.mock.MockRole;
import com.muchencute.biz.keycloak.environment.mock.MockScope;
import com.muchencute.biz.keycloak.request.NewRoleRequest;
import com.muchencute.biz.keycloak.service.KeycloakRoleService;
import com.muchencute.biz.keycloak.service.KeycloakUserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin", authorities = "role:crud")
@TestPropertySource(properties = "keycloak.protected-role-names=super_admin")
@ContextConfiguration(classes = RoleController.class)
class RoleControllerTest extends KeycloakTestEnvironment {

  final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  Environment environment;
  @Autowired
  KeycloakUserService keycloakUserService;
  @Autowired
  KeycloakRoleService keycloakRoleService;

  @Test
  @MockRole(name = "super_admin")
  @SneakyThrows
  @Transactional("keycloakTransactionManager")
  void role_delete_protected() {

    mockMvc.perform(delete("/role/super_admin"))
      .andExpect(status().isBadRequest())
      .andDo(document("role/delete/protected"));
  }

  @Test
  @MockRole(name = "role_1")
  @SneakyThrows
  void role_stat_exists() {

    mockMvc.perform(head("/role").param("roleName", "role_1"))
      .andExpect(status().isOk())
      .andDo(document("role/stat/exists"));
  }

  @Test
  @SneakyThrows
  void role_state_not_exists() {

    mockMvc.perform(head("/role").param("roleName", "role_2"))
      .andExpect(status().isNotFound())
      .andDo(document("role/stat/not-exists"));
  }

  @Test
  @MockScope(name = "scope_1")
  @MockScope(name = "scope_2")
  @SneakyThrows
  void role_new_ok() {

    final var request = new NewRoleRequest();
    request.setName("role_1");
    request.setScopes(Set.of("scope_1", "scope_2"));

    mockMvc.perform(post("/role")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").isNotEmpty())
      .andExpect(jsonPath("$.name").value("role_1"))
      .andExpect(jsonPath("$.scopes", hasItems("scope_1", "scope_2")))
      .andDo(document("role/new/ok"));
  }

  @Test
  @MockRole(name = "role_1")
  @SneakyThrows
  void role_new_exists() {

    final var request = new NewRoleRequest();
    request.setName("role_1");
    request.setScopes(Set.of("scope_1", "scope_2"));

    mockMvc.perform(post("/role")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isConflict())
      .andDo(document("role/new/exists"));
  }

  @Test
  @MockScope(name = "scope_1")
  @MockScope(name = "scope_2")
  @MockScope(name = "scope_3")
  @MockRole(name = "role_1", scopes = {"scope_1", "scope_2"})
  @SneakyThrows
  void role_update_ok() {

    final var roleId = keycloakRoleService.getRole("role_1").getId();
    mockMvc.perform(put("/role/role_1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Set.of("scope_1", "scope_3"))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(roleId))
      .andExpect(jsonPath("$.name").value("role_1"))
      .andExpect(jsonPath("$.scopes", hasItems("scope_1", "scope_3")))
      .andExpect(jsonPath("$.scopes.length()", equalTo(2)))
      .andDo(document("role/update/ok"));
  }

  @Test
  @SneakyThrows
  void role_update_not_exists() {

    mockMvc.perform(put("/role/role_2")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Set.of("scope_1", "scope_3"))))
      .andExpect(status().isNotFound())
      .andDo(document("role/update/not-exists"));
  }

  @Test
  @MockRole(name = "role_1")
  @MockRole(name = "role_2")
  @SneakyThrows
  void role_get_list() {

    mockMvc.perform(get("/role"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$").isNotEmpty())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$.[*].name", hasItems("role_1", "role_2")))
      .andDo(document("role/get-list/ok"));
  }

  @Test
  @MockScope(name = "scope_1")
  @MockScope(name = "scope_2")
  @MockRole(name = "role_1", scopes = {"scope_1", "scope_2"})
  @SneakyThrows
  void role_get_single_ok() {

    final var roleId = keycloakRoleService.getRole("role_1").getId();
    mockMvc.perform(get("/role/role_1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(roleId))
      .andExpect(jsonPath("$.name").value("role_1"))
      .andExpect(jsonPath("$.scopes", hasItems("scope_1", "scope_2")))
      .andDo(document("role/get-single/ok"));
  }

  @Test
  @SneakyThrows
  void role_get_single_not_exists() {

    mockMvc.perform(get("/role/role_2"))
      .andExpect(status().isNotFound())
      .andDo(document("role/get-single/not-exists"));
  }

  @Test
  @MockRole(name = "role_1")
  @SneakyThrows
  void role_rename_ok() {

    mockMvc.perform(post("/role/role_1:rename")
        .contentType(MediaType.TEXT_PLAIN)
        .content("role_2"))
      .andExpect(status().isOk())
      .andDo(document("role/rename/ok"));
  }

  @Test
  @SneakyThrows
  void role_rename_not_found() {

    mockMvc.perform(post("/role/role_1:rename")
        .contentType(MediaType.TEXT_PLAIN)
        .content("role_3"))
      .andExpect(status().isNotFound())
      .andDo(document("role/rename/not-exists"));
  }

  @Test
  @MockRole(name = "role_1")
  @MockRole(name = "role_2")
  @SneakyThrows
  void role_rename_exists() {

    mockMvc.perform(post("/role/role_1:rename")
        .contentType(MediaType.TEXT_PLAIN)
        .content("role_2"))
      .andExpect(status().isConflict())
      .andDo(document("role/rename/exists"));
  }

  @Test
  @MockRole(name = "role_1")
  @MockRole(name = "super_admin")
  @SneakyThrows
  void role_delete_ok() {

    mockMvc.perform(delete("/role/role_1"))
      .andExpect(status().isOk())
      .andDo(document("role/delete/ok"));

  }

  @Test
  @MockRole(name = "super_admin")
  @SneakyThrows
  void role_delete_not_exists() {

    mockMvc.perform(delete("/role/role_1"))
      .andExpect(status().isNotFound())
      .andDo(document("role/delete/not-exists"));
  }
}
