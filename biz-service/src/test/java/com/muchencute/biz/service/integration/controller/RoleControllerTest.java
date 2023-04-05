package com.muchencute.biz.service.integration.controller;

import com.muchencute.test.environment.KeycloakTestEnvironment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WithMockUser(username = "admin", authorities = "role:crud")
class RoleControllerTest extends KeycloakTestEnvironment {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @SneakyThrows
  void deleteRole() {

    mockMvc.perform(delete("/role/超级管理员"))
        .andExpect(status().isBadRequest());
  }
}